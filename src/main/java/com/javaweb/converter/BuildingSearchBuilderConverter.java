package com.javaweb.converter;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.enums.TypeCode;
import com.javaweb.exception.InvalidRequestException;
import com.javaweb.security.utils.SecurityUtils;
import com.javaweb.utils.MapUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
public class BuildingSearchBuilderConverter {
    public BuildingSearchBuilder toBuildingSearchBuilder(Map<String, Object> params, List<String> typeCodes) {
        Long staffId = SecurityUtils.getAuthorities().contains("ROLE_STAFF")
                ? SecurityUtils.getPrincipal().getId()
                : MapUtils.getObject(params, "staffId", Long.class);

        return new BuildingSearchBuilder.BuildingBuilder()
                .setName(MapUtils.getObject(params, "name", String.class))
                .setFloorArea(MapUtils.getObject(params, "floorArea", Integer.class))
                .setWard(MapUtils.getObject(params, "ward", String.class))
                .setStreet(MapUtils.getObject(params, "street", String.class))
                .setDistrict(MapUtils.getObject(params, "district", String.class))
                .setNumberOfBasement(MapUtils.getObject(params, "numberOfBasement", Long.class))
                .setTypeCode(normalizeTypeCodes(typeCodes))
                .setManagerName(MapUtils.getObject(params, "managerName", String.class))
                .setManagerPhoneNumber(MapUtils.getObject(params, "managerPhone", String.class))
                .setRentPriceFrom(MapUtils.getObject(params, "rentPriceFrom", Integer.class))
                .setrentPriceTo(MapUtils.getObject(params, "rentPriceTo", Integer.class))
                .setRentAreaFrom(MapUtils.getObject(params, "rentAreaFrom", Long.class))
                .setRentAreaTo(MapUtils.getObject(params, "rentAreaTo", Long.class))
                .setStaffId(staffId)
                .build();
    }

    private List<String> normalizeTypeCodes(List<String> typeCodes) {
        if (typeCodes == null) {
            return new ArrayList<>();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String code : typeCodes) {
            try {
                normalized.add(TypeCode.valueOf(code).name());
            } catch (IllegalArgumentException | NullPointerException exception) {
                throw new InvalidRequestException("Loại tòa nhà tìm kiếm không hợp lệ.");
            }
        }
        return new ArrayList<>(normalized);
    }
}
