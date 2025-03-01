package com.example.demo_fixerio.util;

import com.example.demo_fixerio.controller.dto.FixerIORequest;
import com.example.demo_fixerio.service.dto.VerifiedFixerIORequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static com.example.demo_fixerio.util.DateTimeUtil.YYYY_MM_DD;

public final class VerifyUtil {
    private VerifyUtil() {}

    public static VerifiedFixerIORequest verifyFixerIORequest(final FixerIORequest request) {
        VerifiedFixerIORequest verifiedRequest = new VerifiedFixerIORequest();

        LocalDate currentDate = DateTimeUtil.getCurrentDate();
        // Check date from
        Optional<String> dateFrom = request.getDateFrom();
        if (dateFrom.isPresent() && DateTimeUtil.verifyDateValue(dateFrom.get(), YYYY_MM_DD)) {
            verifiedRequest.setDateFrom(dateFrom.get());
        } else {
            verifiedRequest.setDateFrom(String.valueOf(currentDate));
        }
        // Check date to
        Optional<String> dateTo = request.getDateTo();
        if (dateTo.isPresent() && DateTimeUtil.verifyDateValue(dateTo.get(), YYYY_MM_DD)) {
            verifiedRequest.setDateTo(dateTo.get());
            /*
            if dateTo < dateFrom then
                dateFrom = dateTo
             */
            if (DateTimeUtil.compareDate(verifiedRequest.getDateTo(), verifiedRequest.getDateFrom()) < 0) {
                verifiedRequest.setDateFrom(verifiedRequest.getDateTo());
            }
        } else {
            verifiedRequest.setDateTo(String.valueOf(currentDate));
        }

        // Check offset
        Optional<Integer> offset = request.getOffset();
        if (offset.isPresent() && offset.get() >= 0) {
            verifiedRequest.setOffset(offset.get());
        } else {
            verifiedRequest.setOffset(0);
        }
        // Check limit
        Optional<Integer> limit = request.getLimit();
        if (limit.isPresent() && limit.get() >= 20) {
            verifiedRequest.setLimit(limit.get());
        } else {
            verifiedRequest.setLimit(20);
        }

        return verifiedRequest;
    }

    public static PageRequest verifyPageRequest(Integer page, Integer size) {
        int getPage = Objects.nonNull(page) ? page : 0;
        int getPageSize = Objects.nonNull(size) && size >= 1 ? size : 20;
        return PageRequest.of(getPage, getPageSize, Sort.by("date").descending());
    }
}
