package com.example.demo_fixerio;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SomeTests {
    @Test
    public void convertTimestamp() {
        int timestamp = 1569317347;
        Instant instant = Instant.ofEpochSecond(timestamp);
        System.out.println(instant);
    }

    @Test
    public void convertMapToString() {
        Map<String, Double> map = new HashMap<>();
        map.put("key1", 8.123456);
        map.put("key2", 6.54321);
        System.out.println(map.toString());
    }

    @Test
    public void convertStringToMap() {
        String stringMap = "{key1=8.123456, key2=6.54321, key3=95.32106}";
        System.out.println(stringMap.matches("^\\{[\\w=., ]*}$"));
        String[] stringArrayMap = stringMap.replace("{", "")
                .replace("}", "").split(", ");
        Map<String, Double> map = new HashMap<>();
        for (String stringData : stringArrayMap) {
            String[] keyValue = stringData.split("=");
            map.put(keyValue[0], Double.parseDouble(keyValue[1]));
        }
        System.out.println(map);
    }

    @Test
    public void convertStringToMap2() {
        String stringMap = "{key1=8.123456, key2=6.54321, key3=95.32106}";
        List<String> keyAndValues = Arrays.asList(stringMap.replace("{", "")
                .replace("}", "").split(", "));
        Map<String, Double> map = keyAndValues.stream()
                .collect(Collectors.toMap(key -> key.split("=")[0],
                        value -> Double.parseDouble(value.split("=")[1])));
        System.out.println(map);
    }

    @Test
    public void getCurrentDate() {
        LocalDate date = LocalDate.now();
        System.out.println(date);
    }

    @Test
    public void newAndNullObject() {
        Object o = new Object();
        Assert.assertEquals(true, Objects.nonNull(o));
    }
}
