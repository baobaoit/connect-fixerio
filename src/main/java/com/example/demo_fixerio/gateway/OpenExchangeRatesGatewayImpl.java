package com.example.demo_fixerio.gateway;

import com.example.demo_fixerio.gateway.dto.GatewayResponse;
import com.example.demo_fixerio.gateway.dto.HistoricalGatewayResponse;
import com.example.demo_fixerio.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile({"oxr-prod", "oxr-dev"})
public final class OpenExchangeRatesGatewayImpl implements ExchangeRatesGateway {
    private static final String ENDPOINT_LATEST = "latest";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${exchange-rates.base-url}")
    private String baseUrl;

    @Value("${exchange-rates.access-key}")
    private String defaultAccessKey;

    @Value("${exchange-rates.default-base-currency}")
    private String defaultBaseCurrency;

    @Value("${exchange-rates.default-symbols}")
    private String defaultSymbols;

    @Override
    public GatewayResponse getLatestExchangeRates(String accessKey, String baseCurrency) {
        final String latestUri = generateFullApiEndpointUri(ENDPOINT_LATEST);
        GatewayResponse response = restTemplate.getForObject(latestUri, GatewayResponse.class);
        if (response != null) {
            response.setDate(DateTimeUtil.getDateFromEpochSecond(response.getTimestamp()));
            response.setSuccess(true);
        }
        return response;
    }

    @Override
    public HistoricalGatewayResponse getExchangeRatesFromDate(String date, String accessKey, String baseCurrency) {
        final String historicalUri = generateFullApiEndpointUri(String.format("historical/%s", date));
        HistoricalGatewayResponse response = restTemplate.getForObject(historicalUri, HistoricalGatewayResponse.class);
        if (response != null) {
            response.setHistorical(true);
        }
        return response;
    }

    private String generateFullApiEndpointUri(String endpoint) {
        return new StringBuilder(baseUrl).append(endpoint)
                .append(".json?app_id=").append(defaultAccessKey)
                .append("&symbols=").append(defaultSymbols)
                .append("&base=").append(defaultBaseCurrency)
                .toString();
    }
}
