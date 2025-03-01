package com.example.demo_fixerio.service;

import com.example.demo_fixerio.controller.dto.FixerIORequest;
import com.example.demo_fixerio.controller.dto.FixerIOResponse;
import com.example.demo_fixerio.domain.model.PagingSearchResults;

public interface ExchangeRatesService {
    PagingSearchResults<FixerIOResponse> getAll(Integer page, Integer size);

    FixerIOResponse getLatestExchangeRates(String accessKey, String baseCurrency);

    FixerIOResponse getExchangeRatesFromDate(String date, String accessKey, String baseCurrency);

    PagingSearchResults<FixerIOResponse> getExchangeRatesFromRange(FixerIORequest request, String accessKey, String baseCurrency);

    FixerIOResponse updateExchangeRatesOfDate(String date, String accessKey, String baseCurrency);

    PagingSearchResults<FixerIOResponse> updateExchangeRatesFromRage(FixerIORequest request, String accessKey, String baseCurrency);
}
