package com.example.demo_fixerio.service.mapper;

import com.example.demo_fixerio.controller.dto.FixerIOResponse;
import com.example.demo_fixerio.domain.ExchangeRatesEntity;
import com.example.demo_fixerio.gateway.dto.GatewayResponse;
import com.example.demo_fixerio.util.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public final class ExchangeRatesMapper {
    public FixerIOResponse toFixerIOResponse(final ExchangeRatesEntity entity) {
        return FixerIOResponse.builder()
                .date(StringUtils.isNoneBlank(entity.getDate()) ? entity.getDate() : StringUtils.EMPTY)
                .rates(extractExchangeRatesFromEntity(entity))
                .build();
    }

    public Map<String, Double> extractExchangeRatesFromEntity(final ExchangeRatesEntity entity) {
        Map<String, Double> rates = new HashMap<>();
        if (Objects.nonNull(entity)) {
            rates.put("AED", entity.getAed());
            rates.put("CHF", entity.getChf());
            rates.put("EUR", entity.getEur());
            rates.put("GBP", entity.getGbp());
            rates.put("USD", entity.getUsd());
            rates.put("ZAR", entity.getZar());
        }
        return rates;
    }

    public ExchangeRatesEntity toExchangeRatesEntity(final GatewayResponse gatewayResponse) throws IllegalAccessException {
        ExchangeRatesEntity entity = ExchangeRatesEntity.builder()
                .date(StringUtils.isNoneBlank(gatewayResponse.getDate()) ? gatewayResponse.getDate() : StringUtils.EMPTY)
                .build();

        entity.setCurrencyRates(standardizedCurrencyRates(gatewayResponse.getRates()));

        return entity;
    }

    private Map<String, Double> standardizedCurrencyRates(Map<String, ?> currencyRates) {
        Map<String, Double> standardized = new HashMap<>();

        Class<?> clazz = MapUtils.getValueType(currencyRates);
        boolean isList = Arrays.asList(clazz.getInterfaces()).contains(List.class);

        if (isList) {
            standardized.put("USD", 1.0);
        }

        currencyRates.forEach((currency, rates) -> {
            String ratesAsString = String.valueOf(rates);
            if (isList) {
                @SuppressWarnings("unchecked")
                List<String> ratesInString = (List<String>) rates;

                currency = String.join("", currency.split("USD"));
                ratesAsString = ratesInString.get(0);
            }
            standardized.put(currency, Double.parseDouble(ratesAsString));
        });

        return standardized;
    }
}
