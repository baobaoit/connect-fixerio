package com.example.demo_fixerio.gateway;


import com.example.demo_fixerio.gateway.dto.GatewayResponse;
import com.example.demo_fixerio.gateway.dto.HistoricalGatewayResponse;

public interface ExchangeRatesGateway {
    GatewayResponse getLatestExchangeRates(String accessKey, String baseCurrency);

    HistoricalGatewayResponse getExchangeRatesFromDate(String date, String accessKey, String baseCurrency);
}
