package com.example.demo_fixerio.service;

import com.example.demo_fixerio.config.mail.Constants;
import com.example.demo_fixerio.controller.dto.FixerIORequest;
import com.example.demo_fixerio.controller.dto.FixerIOResponse;
import com.example.demo_fixerio.domain.ExchangeRatesEntity;
import com.example.demo_fixerio.domain.model.PagingSearchResults;
import com.example.demo_fixerio.exception.EntityNotFoundException;
import com.example.demo_fixerio.exception.InvalidDateException;
import com.example.demo_fixerio.gateway.ExchangeRatesGateway;
import com.example.demo_fixerio.gateway.dto.GatewayResponse;
import com.example.demo_fixerio.gateway.dto.HistoricalGatewayResponse;
import com.example.demo_fixerio.mail.EmailMessageHelper;
import com.example.demo_fixerio.mail.core.SimpleEmailSender;
import com.example.demo_fixerio.mail.model.EmailMessage;
import com.example.demo_fixerio.repository.ExchangeRatesRepository;
import com.example.demo_fixerio.service.dto.VerifiedFixerIORequest;
import com.example.demo_fixerio.service.mapper.ExchangeRatesMapper;
import com.example.demo_fixerio.service.mapper.PagingSearchResultsMapper;
import com.example.demo_fixerio.util.DateTimeUtil;
import com.example.demo_fixerio.util.VerifyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.demo_fixerio.util.DateTimeUtil.MM_DD_YYYY_HH_MM_SS;
import static com.example.demo_fixerio.util.DateTimeUtil.YYYY_MM_DD;

@Service
@Slf4j
public final class ExchangeRatesServiceImpl implements ExchangeRatesService {
    private static final String OPEN_EXCHANGE_RATES_PROFILE = "oxr";
    private static final String CURRENCY_CLOUD_PROFILE = "currencycloud";
    private static final String CURRENCY_CLOUD = "Currencycloud";

    private final ExchangeRatesRepository exchangeRatesRepository;
    private final ExchangeRatesMapper exchangeRatesMapper;
    private final ExchangeRatesGateway exchangeRatesGateway;
    private final PagingSearchResultsMapper pagingSearchResultsMapper;
    private final String exchangeRatesSite;

    @Value("${email.noreply}")
    private String noreplyEmail;

    private SimpleEmailSender simpleEmailSender;

    @Value("${ewm.notification-service.technical-support}")
    private String technicalSupportEmail;

    @Value("${period.fail-attempt-threshold-amount.fixer-attempts}")
    private int failedAttemptThreshold;

    @Value("${period.delay-time}")
    private int delayTime;

    @Autowired
    public ExchangeRatesServiceImpl(ExchangeRatesRepository exchangeRatesRepository,
                                    ExchangeRatesMapper exchangeRatesMapper,
                                    ExchangeRatesGateway fixerIOGatewayImpl,
                                    ExchangeRatesGateway openExchangeRatesGatewayImpl,
                                    ExchangeRatesGateway currencycloudGatewayImpl,
                                    PagingSearchResultsMapper pagingSearchResultsMapper,
                                    Environment environment) {
        this.exchangeRatesRepository = exchangeRatesRepository;
        this.exchangeRatesMapper = exchangeRatesMapper;
        this.pagingSearchResultsMapper = pagingSearchResultsMapper;
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(e -> e.contains(OPEN_EXCHANGE_RATES_PROFILE))) {
            this.exchangeRatesGateway = openExchangeRatesGatewayImpl;
            exchangeRatesSite = "Open Exchange Rates";
        } else if (Arrays.stream(environment.getActiveProfiles()).anyMatch(e -> e.contains(CURRENCY_CLOUD_PROFILE))) {
            this.exchangeRatesGateway = currencycloudGatewayImpl;
            exchangeRatesSite = "Currencycloud";
        } else {
            this.exchangeRatesGateway = fixerIOGatewayImpl;
            exchangeRatesSite = "Fixer.IO";
        }
        log.info("Using {} API", exchangeRatesSite);
    }

    @Override
    public PagingSearchResults<FixerIOResponse> getAll(Integer page, Integer size) {
        Page<ExchangeRatesEntity> paging = exchangeRatesRepository.findAll(VerifyUtil.verifyPageRequest(page, size));
        PagingSearchResults<FixerIOResponse> results = new PagingSearchResults<>();
        results.setOffset((int) paging.getPageable().getOffset());
        results.setLimit(paging.getPageable().getPageSize());
        results.setTotal(paging.getTotalElements());
        results.setResults(paging.getContent().stream().map(exchangeRatesMapper::toFixerIOResponse).collect(Collectors.toList()));
        return results;
    }

    @Override
    public FixerIOResponse getLatestExchangeRates(String accessKey, String baseCurrency) {
        Optional<ExchangeRatesEntity> optionalEntity = exchangeRatesRepository.findById(String.valueOf(DateTimeUtil.getCurrentDate()));
        if (optionalEntity.isPresent()) {
            return exchangeRatesMapper.toFixerIOResponse(optionalEntity.get());
        }

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

        AtomicReference<ExchangeRatesEntity> entity = new AtomicReference<>(new ExchangeRatesEntity());
        AtomicInteger attempts = new AtomicInteger();

        Runnable task = () -> {
            try {
                GatewayResponse gatewayResponse = exchangeRatesGateway.getLatestExchangeRates(accessKey, baseCurrency);
                if (Objects.nonNull(gatewayResponse) && gatewayResponse.isSuccess() && Objects.nonNull(gatewayResponse.getDate())) {
                    ExchangeRatesEntity todayExchangeRatesEntity = exchangeRatesMapper.toExchangeRatesEntity(gatewayResponse);

                    if (CURRENCY_CLOUD.equals(exchangeRatesSite) && gatewayResponse.getRates().isEmpty()) {
                        Function<ExchangeRatesEntity, ExchangeRatesEntity> clone = ExchangeRatesEntity::clone;
                        todayExchangeRatesEntity = exchangeRatesRepository.findFirst1ByOrderByDateDesc()
                                .map(clone.andThen(exchangeRatesEntity -> {
                                    exchangeRatesEntity.setDate(gatewayResponse.getDate());

                                    return exchangeRatesEntity;
                                }))
                                .orElseThrow(() -> new RestClientException("The latest exchange rates not found!"));
                    }

                    entity.set(exchangeRatesRepository.save(todayExchangeRatesEntity));
                }
            } catch (RestClientException | IllegalAccessException e) {
                attempts.addAndGet(1);
                log.error("getLatestExchangeRates with throws {}", e.getMessage());
                e.printStackTrace();
            }
        };

        ScheduledFuture<?> schedule = ses.scheduleAtFixedRate(task, 0, delayTime, TimeUnit.MINUTES);

        while (attempts.get() <= failedAttemptThreshold) {
            if(attempts.get() == failedAttemptThreshold) {
                schedule.cancel(true);
                ses.shutdown();
                sendMailNotifyGetExchangeRateError();
                break;
            }
        }

        schedule.cancel(true);
        ses.shutdown();

        return exchangeRatesMapper.toFixerIOResponse(entity.get());
    }

    public void sendMailNotifyGetExchangeRateError() {
        Set<String> toEmails = new HashSet<>(Arrays.asList(technicalSupportEmail.split(Pattern.quote(";"))));
        EmailMessage emailMessage = EmailMessageHelper.builder()
                .emailFrom(noreplyEmail)
                .emailTo(toEmails)
                .subject(() -> Constants.GET_EXCHANGE_RATE_ERROR_SUBJECT)
                .templateName(Constants.GET_FIXERIO_EXCHANGE_RATE_ERROR)
                .templateProperties(() -> {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern(MM_DD_YYYY_HH_MM_SS)));
                    properties.put("site", exchangeRatesGateway);

                    return properties;
                })
                .build();

        try {
            simpleEmailSender.sendEmail(emailMessage);

            log.info("Sending get fixer exchange rate error notification successfully!");
        } catch (MessagingException e) {
            log.error("Sending get fixer exchange rate error notification failed because: {}", e.getMessage());
        }
    }

    @Override
    public FixerIOResponse getExchangeRatesFromDate(String date, String accessKey, String baseCurrency) {
        if (!DateTimeUtil.verifyDateValue(date, YYYY_MM_DD)) {
            throw new InvalidDateException();
        }

        Optional<ExchangeRatesEntity> optionalEntity = exchangeRatesRepository.findById(date);
        if (optionalEntity.isPresent()) {
            return exchangeRatesMapper.toFixerIOResponse(optionalEntity.get());
        }

        ExchangeRatesEntity entity = new ExchangeRatesEntity();
        HistoricalGatewayResponse gatewayResponse = exchangeRatesGateway.getExchangeRatesFromDate(date, accessKey,
                baseCurrency);
        if (Objects.nonNull(gatewayResponse) && gatewayResponse.isSuccess() && gatewayResponse.isHistorical()) {
            try {
                entity = exchangeRatesRepository.save(exchangeRatesMapper.toExchangeRatesEntity(gatewayResponse));
            } catch (Exception e) {
                log.error("getExchangeRatesFromDate with throws {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return exchangeRatesMapper.toFixerIOResponse(entity);
    }

    @Override
    public PagingSearchResults<FixerIOResponse> getExchangeRatesFromRange(FixerIORequest request, String accessKey,
                                                                          String baseCurrency) {
        VerifiedFixerIORequest verifiedRequest = VerifyUtil.verifyFixerIORequest(request);
        List<LocalDate> dates = DateTimeUtil.listDateFromRange(verifiedRequest.getDateFrom(), verifiedRequest.getDateTo());
        List<FixerIOResponse> responses = new ArrayList<>();
        for (LocalDate localDate : dates) {
            responses.add(getExchangeRatesFromDate(String.valueOf(localDate), accessKey, baseCurrency));
        }
        return pagingSearchResultsMapper.toPaging(responses, verifiedRequest);
    }

    @Override
    public FixerIOResponse updateExchangeRatesOfDate(String date, String accessKey, String baseCurrency) {
        if (!DateTimeUtil.verifyDateValue(date, YYYY_MM_DD)) {
            throw new InvalidDateException();
        }

        ExchangeRatesEntity entity = exchangeRatesRepository.findById(date).orElseThrow(EntityNotFoundException::new);
        Optional<String> oldBaseCurrency = exchangeRatesMapper.extractExchangeRatesFromEntity(entity).entrySet().stream()
                .filter(c -> c.getValue() == 1.0)
                .map(Map.Entry::getKey)
                .findFirst();
        if (oldBaseCurrency.isPresent() && StringUtils.isNoneBlank(baseCurrency) &&
                !oldBaseCurrency.get().equalsIgnoreCase(baseCurrency)) {
            HistoricalGatewayResponse gatewayResponse = exchangeRatesGateway.getExchangeRatesFromDate(date, accessKey,
                    baseCurrency);
            if (Objects.nonNull(gatewayResponse) && gatewayResponse.isSuccess() && gatewayResponse.isHistorical()) {
                try {
                    entity = exchangeRatesRepository.save(exchangeRatesMapper.toExchangeRatesEntity(gatewayResponse));
                } catch (Exception e) {
                    log.error("updateExchangeRatesOfDate with throws {}", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return exchangeRatesMapper.toFixerIOResponse(entity);
    }

    @Override
    public PagingSearchResults<FixerIOResponse> updateExchangeRatesFromRage(FixerIORequest request, String accessKey, String baseCurrency) {
        VerifiedFixerIORequest verifiedRequest = VerifyUtil.verifyFixerIORequest(request);
        List<LocalDate> dates = DateTimeUtil.listDateFromRange(verifiedRequest.getDateFrom(), verifiedRequest.getDateTo());
        List<FixerIOResponse> responses = new ArrayList<>();
        for (LocalDate localDate : dates) {
            responses.add(updateExchangeRatesOfDate(String.valueOf(localDate), accessKey, baseCurrency));
        }
        return pagingSearchResultsMapper.toPaging(responses, verifiedRequest);
    }

    @Autowired
    public void setSimpleEmailSender(SimpleEmailSender simpleEmailSender) {
        this.simpleEmailSender = simpleEmailSender;
    }
}
