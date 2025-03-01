package com.example.demo_fixerio.util;

import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class DateTimeUtil {
    private DateTimeUtil() {}

    public static final String MM_DD_YYYY_HH_MM_SS = "MM-dd-yyyy HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static boolean verifyDateValue(String date, String pattern) {
        try {
            return Objects.nonNull(toLocalDate(date, pattern));
        } catch (DateTimeParseException e) {
            log.error("verifyDateValue parse {} throws {}", date, e.getMessage());
        }
        return false;
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static List<LocalDate> listDateFromRange(String from, String to) {
        LocalDate start = LocalDate.parse(from);
        LocalDate end = LocalDate.parse(to);

        List<LocalDate> totalDates = new ArrayList<>();
        while (!end.isBefore(start)) {
            log.debug( "{}", end);
            totalDates.add(end);
            end = end.minusDays(1);
        }
        return totalDates;
    }

    /**
     * Compares date1Value to date2Value date.
     *
     * @param date1Value the first date value as String following this format yyyy-MM-dd
     * @param date2Value the second date value as String following this format yyyy-MM-dd
     * @return the comparator value, negative if less, positive if greater
     */
    public static int compareDate(String date1Value, String date2Value) {
        int result = 0;
        if (verifyDateValue(date1Value, YYYY_MM_DD) && verifyDateValue(date2Value, YYYY_MM_DD)) {
            try {
                result = toLocalDate(date1Value, YYYY_MM_DD).compareTo(toLocalDate(date2Value, YYYY_MM_DD));
            } catch (DateTimeParseException e) {
                log.error("compareDate {} with {} throws {}", date1Value, date2Value, e.getMessage());
            }
        }
        return result;
    }

    private static LocalDate toLocalDate(String dateValue, String pattern) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateValue, formatter);
    }

    public static String getDateFromEpochSecond(long epochSecond) {
        try {
            Instant fromEpochSecond = Instant.ofEpochSecond(epochSecond);
            LocalDateTime dateTime = LocalDateTime.ofInstant(fromEpochSecond, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
            return formatter.format(dateTime);
        } catch (DateTimeException e) {
            log.error("Failed when parsing '{}': {}", epochSecond, e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static String getCurrentDateFormatted() {
        LocalDate now = getCurrentDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return formatter.format(now);
    }
}
