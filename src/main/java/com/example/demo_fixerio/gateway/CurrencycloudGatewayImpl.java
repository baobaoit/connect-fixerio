package com.example.demo_fixerio.gateway;

import com.example.demo_fixerio.gateway.dto.GatewayResponse;
import com.example.demo_fixerio.gateway.dto.HistoricalGatewayResponse;
import com.example.demo_fixerio.gateway.dto.currencycloud.CCAuthenticateResponse;
import com.example.demo_fixerio.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Profile({"currencycloud-prod", "currencycloud-dev"})
public final class CurrencycloudGatewayImpl implements ExchangeRatesGateway {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${exchange-rates.base-url}")
    private String baseUrl;

    @Value("${exchange-rates.access-key}")
    private String defaultAccessKey;

    @Value("${exchange-rates.login-id}")
    private String defaultLoginId;

    @Value("${exchange-rates.default-base-currency}")
    private String defaultBaseCurrency;

    @Value("${exchange-rates.default-symbols}")
    private String defaultSymbols;

    @Override
    public GatewayResponse getLatestExchangeRates(String accessKey, String baseCurrency) {
        final String xAuthToken = getXAuthToken();
        if (xAuthToken == null) {
            throw new RestClientException("Can't get the X-Auth-Token");
        }

        final String getBasicRatesUri = String.format("%s/rates/find", baseUrl);
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(getBasicRatesUri)
                .queryParam("currency_pair", getCurrencyPairs());

        RequestEntity<Void> requestEntity = RequestEntity.get(uriBuilder.build())
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token", xAuthToken)
                .build();

        ResponseEntity<GatewayResponse> response = restTemplate.exchange(requestEntity, GatewayResponse.class);

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            GatewayResponse body = response.getBody();
            if (body != null) {
                body.setSuccess(true);
                body.setDate(DateTimeUtil.getCurrentDateFormatted());

                return body;
            }
        }

        throw new RestClientException("Can't get the exchange rates");
    }

    @Override
    public HistoricalGatewayResponse getExchangeRatesFromDate(String date, String accessKey, String baseCurrency) {
        throw new UnsupportedOperationException("This operation is not implemented");
    }

    public String getXAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("api_key", defaultAccessKey);
        form.add("login_id", defaultLoginId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);

        final String authenticateUri = String.format("%s/authenticate/api", baseUrl);

        ResponseEntity<CCAuthenticateResponse> authenticatedEntity = restTemplate
                .postForEntity(authenticateUri, requestEntity, CCAuthenticateResponse.class);

        if (HttpStatus.OK.equals(authenticatedEntity.getStatusCode())) {
            return Objects.requireNonNull(authenticatedEntity.getBody()).getAuthToken();
        }

        return null;
    }

    private String getCurrencyPairs() {
        return Stream.of(defaultSymbols.split(","))
                .map(e -> String.format("%s%s", defaultBaseCurrency, e))
                .collect(Collectors.joining(","));
    }
}
