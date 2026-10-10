package com.javaweb.utils;

import com.javaweb.exception.InvalidRequestException;

import java.util.Map;

public final class MapUtils {
    private MapUtils() {
    }

    public static <T> T getObject(Map<String, Object> values, String key, Class<T> targetType) {
        Object rawValue = values.get(key);
        if (rawValue == null) {
            return null;
        }
        String value = rawValue.toString().trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            Object converted;
            if (Long.class.equals(targetType)) {
                converted = Long.valueOf(value);
            } else if (Integer.class.equals(targetType)) {
                converted = Integer.valueOf(value);
            } else if (Double.class.equals(targetType)) {
                converted = Double.valueOf(value);
            } else if (String.class.equals(targetType)) {
                converted = value;
            } else {
                throw new IllegalArgumentException("Unsupported target type: " + targetType.getName());
            }
            return targetType.cast(converted);
        } catch (NumberFormatException exception) {
            throw new InvalidRequestException("Tham số " + key + " phải là một số hợp lệ.");
        }
    }
}
