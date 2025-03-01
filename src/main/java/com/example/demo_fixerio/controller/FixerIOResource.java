package com.example.demo_fixerio.controller;

import com.example.demo_fixerio.controller.dto.FixerIORequest;
import com.example.demo_fixerio.controller.dto.FixerIOResponse;
import com.example.demo_fixerio.domain.model.PagingSearchResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = {"Exchange rates"})
public interface FixerIOResource {
    @ApiOperation(value = "Get all exchange rates from database")
    PagingSearchResults<FixerIOResponse> getAll(Integer page, Integer size);

    @ApiOperation(value = "Get latest exchange rates")
    FixerIOResponse getLatestExchangeRates(String accessKey, String baseCurrency);

    @ApiOperation(value = "Get exchange rates from a specific date")
    FixerIOResponse getExchangeRatesFromDate(String date, String accessKey, String baseCurrency);

    @ApiOperation(value = "Get exchange rates from a range of date")
    PagingSearchResults<FixerIOResponse> getExchangeRatesFromRange(FixerIORequest request, String accessKey, String baseCurrency);

    @ApiOperation(value = "Update exchange rates of a specific date")
    FixerIOResponse updateExchangeRatesOfDate(String date, String accessKey, String baseCurrency);

    @ApiOperation(value = "Update exchange rates from a range of date")
    PagingSearchResults<FixerIOResponse> updateExchangeRatesFromRage(FixerIORequest request, String accessKey, String baseCurrency);
}
