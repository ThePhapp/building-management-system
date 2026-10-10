package com.javaweb.enums;

import java.util.Map;
import java.util.TreeMap;

public enum District {
    QUAN_1 ("Quận 1"),
    QUAN_2 ("Quận 2"),
    QUAN_3 ("Quận 3"),
    QUAN_4 ("Quận 4"),
    QUAN_5 ("Quận 5"),
    QUAN_6 ("Quận 6"),
    QUAN_7 ("Quận 7"),
    QUAN_8 ("Quận 8"),
    NULL("null");

    private final String districtName;

    District(String districtName) {
        this.districtName = districtName;
    }

    public static Map<String, String> getDistricts() {
        Map<String, String> districts = new TreeMap<>();
        for (District district : District.values()) {
            if (district != NULL) {
                districts.put(district.name(), district.districtName);
            }
        }
        return districts;
    }
}
