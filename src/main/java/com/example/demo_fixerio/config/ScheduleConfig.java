package com.example.demo_fixerio.config;

import com.example.demo_fixerio.controller.dto.FixerIOResponse;
import com.example.demo_fixerio.service.ExchangeRatesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {
    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public ScheduleConfig(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @Scheduled(cron = "0 30 2 * * ?")
    public void getLatestExchangeRatesTask() {
        FixerIOResponse response = exchangeRatesService.getLatestExchangeRates(StringUtils.EMPTY, StringUtils.EMPTY);
        log.info("getLatestExchangeRatesTask returns {}", response);
    }
}
