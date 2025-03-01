package com.example.demo_fixerio.controller;

import com.example.demo_fixerio.controller.dto.FixerIORequest;
import com.example.demo_fixerio.controller.dto.FixerIOResponse;
import com.example.demo_fixerio.domain.model.PagingSearchResults;
import com.example.demo_fixerio.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/fixerio")
public final class FixerIOResourceImpl implements FixerIOResource {
    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public FixerIOResourceImpl(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @GetMapping(value = {"/"})
    @Override
    public PagingSearchResults<FixerIOResponse> getAll(@RequestParam(value = "page", required = false) Integer page,
                                                       @RequestParam(value = "size", required = false) Integer size) {
        return exchangeRatesService.getAll(page, size);
    }

    @GetMapping(value = {"/latest"})
    @Override
    public FixerIOResponse getLatestExchangeRates(@RequestParam(value = "access_key", required = false) String accessKey,
                                                  @RequestParam(value = "base", required = false) String baseCurrency) {
        return exchangeRatesService.getLatestExchangeRates(accessKey, baseCurrency);
    }

    @GetMapping(value = {"/{date}"})
    @Override
    public FixerIOResponse getExchangeRatesFromDate(@PathVariable(value = "date") String date,
                                                    @RequestParam(value = "access_key", required = false) String accessKey,
                                                    @RequestParam(value = "base", required = false) String baseCurrency) {
        return exchangeRatesService.getExchangeRatesFromDate(date, accessKey, baseCurrency);
    }

    @PostMapping(value = {"/exchange-rates-from-range"}, consumes = APPLICATION_JSON_VALUE)
    @Override
    public PagingSearchResults<FixerIOResponse> getExchangeRatesFromRange(@RequestBody @Valid FixerIORequest request,
                                                @RequestParam(value = "access_key", required = false) String accessKey,
                                                @RequestParam(value = "base", required = false) String baseCurrency) {
        return exchangeRatesService.getExchangeRatesFromRange(request, accessKey, baseCurrency);
    }

    @PutMapping(value = {"/{date}"})
    @Override
    public FixerIOResponse updateExchangeRatesOfDate(@PathVariable(value = "date") String date,
                                                @RequestParam(value = "access_key", required = false) String accessKey,
                                                @RequestParam(value = "base", required = false) String baseCurrency) {
        return exchangeRatesService.updateExchangeRatesOfDate(date, accessKey, baseCurrency);
    }

    @PutMapping(value = {"/exchange-rates-from-range"})
    @Override
    public PagingSearchResults<FixerIOResponse> updateExchangeRatesFromRage(@RequestBody @Valid FixerIORequest request,
                                                @RequestParam(value = "access_key", required = false) String accessKey,
                                                @RequestParam(value = "base", required = false) String baseCurrency) {
        return exchangeRatesService.updateExchangeRatesFromRage(request, accessKey, baseCurrency);
    }
}
