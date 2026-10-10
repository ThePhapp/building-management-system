package com.javaweb.converter;

import com.javaweb.entity.BuildingEntity;
import com.javaweb.entity.RentAreaEntity;
import com.javaweb.exception.InvalidRequestException;
import com.javaweb.model.dto.BuildingDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class BuildingConverter {

    public BuildingDTO toBuildingDTO(BuildingEntity entity) {
        BuildingDTO dto = new BuildingDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setWard(entity.getWard());
        dto.setNumberOfBasement(entity.getNumberOfBasement());
        dto.setDistrict(entity.getDistrict());
        dto.setTypeCode(splitTypeCodes(entity.getTypeCode()));
        dto.setManagerName(entity.getManagerName());
        dto.setManagerPhone(entity.getManagerPhone());
        dto.setFloorArea(entity.getFloorArea());
        dto.setRentPrice(entity.getRentPrice() == null ? null : entity.getRentPrice().longValue());
        dto.setStreet(entity.getStreet());
        dto.setStructure(entity.getStructure());
        dto.setDirection(entity.getDirection());
        dto.setLevel(entity.getLevel());
        dto.setRentPriceDescription(entity.getRentPriceDescription());
        dto.setMotoFee(entity.getMotoFee());
        dto.setCarFee(entity.getCarFee());
        dto.setOverTimeFee(entity.getOverTimeFee());
        dto.setElectricityFee(entity.getElectricityFee());
        dto.setDeposit(entity.getDeposit());
        dto.setPayment(entity.getPayment());
        dto.setRentTime(entity.getRentTime());
        dto.setDecorationTime(entity.getDecorationTime());
        dto.setBrokerageFee(entity.getBrokerageFee() == null ? null : entity.getBrokerageFee().toString());
        dto.setNote(entity.getNote());
        dto.setServiceFee(entity.getServiceFee());
        dto.setImage(entity.getImage());
        if (entity.getRentAreas() != null) {
            dto.setRentAreas(entity.getRentAreas().stream()
                    .map(RentAreaEntity::getValue)
                    .filter(value -> value != null)
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }
        return dto;
    }

    public void updateEntity(BuildingDTO dto, BuildingEntity entity) {
        entity.setName(trim(dto.getName()));
        entity.setWard(trimToNull(dto.getWard()));
        entity.setNumberOfBasement(dto.getNumberOfBasement());
        entity.setDistrict(trim(dto.getDistrict()));
        entity.setTypeCode(String.join(",", dto.getTypeCode()));
        entity.setManagerName(trimToNull(dto.getManagerName()));
        entity.setManagerPhone(trimToNull(dto.getManagerPhone()));
        entity.setFloorArea(dto.getFloorArea());
        entity.setRentPrice(dto.getRentPrice() == null ? null : toInteger(dto.getRentPrice(), "Giá thuê quá lớn"));
        entity.setStreet(trim(dto.getStreet()));
        entity.setStructure(trimToNull(dto.getStructure()));
        entity.setDirection(trimToNull(dto.getDirection()));
        entity.setLevel(trimToNull(dto.getLevel()));
        entity.setRentPriceDescription(trimToNull(dto.getRentPriceDescription()));
        entity.setMotoFee(trimToNull(dto.getMotoFee()));
        entity.setCarFee(trimToNull(dto.getCarFee()));
        entity.setOverTimeFee(trimToNull(dto.getOverTimeFee()));
        entity.setElectricityFee(trimToNull(dto.getElectricityFee()));
        entity.setDeposit(trimToNull(dto.getDeposit()));
        entity.setPayment(trimToNull(dto.getPayment()));
        entity.setRentTime(trimToNull(dto.getRentTime()));
        entity.setDecorationTime(trimToNull(dto.getDecorationTime()));
        entity.setBrokerageFee(parseBrokerageFee(dto.getBrokerageFee()));
        entity.setNote(trimToNull(dto.getNote()));
        entity.setServiceFee(trimToNull(dto.getServiceFee()));
        entity.setImage(trimToNull(dto.getImage()));
    }

    private java.util.List<String> splitTypeCodes(String value) {
        if (StringUtils.isBlank(value)) {
            return new ArrayList<>();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private Float parseBrokerageFee(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            float parsed = Float.parseFloat(value.trim());
            if (parsed < 0) {
                throw new InvalidRequestException("Phí môi giới không được âm.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new InvalidRequestException("Phí môi giới phải là một số hợp lệ.");
        }
    }

    private Integer toInteger(Long value, String message) {
        if (value > Integer.MAX_VALUE) {
            throw new InvalidRequestException(message + ".");
        }
        return value.intValue();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
