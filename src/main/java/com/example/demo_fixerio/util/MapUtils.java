package com.example.demo_fixerio.util;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class MapUtils {
    private MapUtils() {}

    public static Class<?> getValueType(Map<?, ?> map) {
        AtomicReference<Class<?>> clazzRef = new AtomicReference<>(Object.class);

        if (map != null && !map.isEmpty()) {
            map.values().stream()
                    .map(Object::getClass)
                    .findFirst()
                    .ifPresent(clazzRef::set);
        }

        return clazzRef.get();
    }
}
