package com.example.demo_fixerio.gateway;

import com.example.demo_fixerio.gateway.dto.GatewayResponse;
import com.example.demo_fixerio.gateway.dto.HistoricalGatewayResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.Stream;

@Service
@Slf4j
@Profile({"prod", "dev"})
public final class FixerIOGatewayImpl implements ExchangeRatesGateway {
    @Value("${exchange-rates.access-key}")
    private String defaultAccessKey;

    @Value("${exchange-rates.default-base-currency}")
    private String defaultBaseCurrency;

    @Value("${exchange-rates.base-url}")
    private String baseUrl;

    private RestTemplate restTemplate;
    private Environment environment;

    @Autowired
    public FixerIOGatewayImpl(Environment environment) {
        this.restTemplate = new RestTemplate();
        this.environment = environment;
    }

    // Endpoint
    private static final String ENDPOINT_LATEST = "latest";

    // Default params
    @Value("${exchange-rates.default-symbols}")
    private String defaultSymbols;

    @Override
    public GatewayResponse getLatestExchangeRates(String accessKey, String baseCurrency) {
        final String latestUri = generateFullApiEndpointUri(ENDPOINT_LATEST, accessKey, baseCurrency);
        return restTemplate.getForObject(latestUri, GatewayResponse.class);
    }

    @Override
    public HistoricalGatewayResponse getExchangeRatesFromDate(String date, String accessKey, String baseCurrency) {
        final String historicalUri = generateFullApiEndpointUri(date, accessKey, baseCurrency);
        try {
            return restTemplate.getForObject(historicalUri, HistoricalGatewayResponse.class);
        } catch (RestClientException e) {
            log.error("getExchangeRatesFromDate {} with historicalUri {} throws {}", date, historicalUri, e.getMessage());
        }
        return null;
    }

    private String getEndpointUri(final String endpoint) {
        return StringUtils.isNoneBlank(endpoint) ? baseUrl + endpoint : baseUrl;
    }

    private String verifyAccessKey(final String accessKey) {
        return StringUtils.isNoneBlank(accessKey) && !defaultAccessKey.equalsIgnoreCase(accessKey) ? accessKey :
                defaultAccessKey;
    }

    private String getBaseCurrency(final String baseCurrency) {
        return StringUtils.isNoneBlank(baseCurrency) &&
                Stream.of("AED", "CHF", "EUR", "GBP", "USD", "ZAR").anyMatch(c -> c.equalsIgnoreCase(baseCurrency))
                && !defaultBaseCurrency.equalsIgnoreCase(baseCurrency) ? baseCurrency : defaultBaseCurrency;
    }

    private String getCurrentActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return CollectionUtils.isNotEmpty(Arrays.asList(activeProfiles)) ? activeProfiles[0] : StringUtils.EMPTY;
    }

    private String generateFullApiEndpointUri(final String endpoint, final String accessKey, final String baseCurrency) {
        final String currentActiveProfile = getCurrentActiveProfile();
        String baseParam = "";
        if ("prod".equalsIgnoreCase(currentActiveProfile)) {
            baseParam = String.format("&base=%s", getBaseCurrency(baseCurrency));
        }

        return String.format("%s?access_key=%s%s&symbols=%s", getEndpointUri(endpoint), verifyAccessKey(accessKey),
                baseParam, defaultSymbols);
    }
}
