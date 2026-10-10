package com.javaweb.service.impl;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.converter.BuildingConverter;
import com.javaweb.converter.BuildingSearchBuilderConverter;
import com.javaweb.entity.AssignmentBuildingEntity;
import com.javaweb.entity.BuildingEntity;
import com.javaweb.entity.RentAreaEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.enums.TypeCode;
import com.javaweb.exception.InvalidRequestException;
import com.javaweb.exception.ResourceNotFoundException;
import com.javaweb.model.dto.AssignmentBuildingDTO;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.response.BuildingSearchResponse;
import com.javaweb.model.response.ResponseDTO;
import com.javaweb.model.response.StaffResponseDTO;
import com.javaweb.model.response.TypeCodeResponseDTO;
import com.javaweb.repository.AssignmentBuildingRepository;
import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.ListRentAreaRepository;
import com.javaweb.repository.UserRepository;
import com.javaweb.service.IBuildingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BuildingService implements IBuildingService {
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BuildingSearchBuilderConverter buildingSearchBuilderConverter;
    @Autowired
    private ListRentAreaRepository listRentAreaRepository;
    @Autowired
    private AssignmentBuildingRepository assignmentBuildingRepository;
    @Autowired
    private BuildingConverter buildingConverter;

    @Override
    @Transactional(readOnly = true)
    public List<BuildingSearchResponse> findAll(Map<String, Object> params, List<String> typeCode) {
        BuildingSearchBuilder builder = buildingSearchBuilderConverter.toBuildingSearchBuilder(params, typeCode);
        return buildingRepository.findAll(builder).stream()
                .map(this::toSearchResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO listStaffs(Long id) {
        requireBuilding(id);
        Set<Long> assignedStaffIds = assignmentBuildingRepository.findByBuildingId_Id(id).stream()
                .map(assignment -> assignment.getStaffId().getId())
                .collect(Collectors.toSet());

        List<StaffResponseDTO> staffResponses = userRepository.findByStatusAndRoles_Code(1, "STAFF").stream()
                .map(staff -> {
                    StaffResponseDTO response = new StaffResponseDTO();
                    response.setFullName(staff.getFullName());
                    response.setStaffId(staff.getId());
                    response.setChecked(assignedStaffIds.contains(staff.getId()) ? "checked" : "");
                    return response;
                })
                .collect(Collectors.toList());

        ResponseDTO result = new ResponseDTO();
        result.setData(staffResponses);
        result.setMessage("Thành công");
        return result;
    }

    @Override
    @Transactional
    public void assignBuilding(AssignmentBuildingDTO request) {
        if (request == null || request.getBuildingId() == null) {
            throw new InvalidRequestException("Tòa nhà cần giao là bắt buộc.");
        }
        BuildingEntity building = requireBuilding(request.getBuildingId());
        List<Long> requestedIds = request.getStaffs() == null
                ? Collections.emptyList()
                : new ArrayList<>(new LinkedHashSet<>(request.getStaffs()));

        List<UserEntity> staffs = requestedIds.isEmpty()
                ? Collections.emptyList()
                : userRepository.findByIdIn(requestedIds);
        if (staffs.size() != requestedIds.size() || staffs.stream().anyMatch(staff -> !isActiveStaff(staff))) {
            throw new InvalidRequestException("Danh sách nhân viên có tài khoản không hợp lệ.");
        }

        assignmentBuildingRepository.deleteByBuildingId_Id(building.getId());
        assignmentBuildingRepository.flush();

        List<AssignmentBuildingEntity> assignments = staffs.stream()
                .map(staff -> {
                    AssignmentBuildingEntity assignment = new AssignmentBuildingEntity();
                    assignment.setBuildingId(building);
                    assignment.setStaffId(staff);
                    return assignment;
                })
                .collect(Collectors.toList());
        assignmentBuildingRepository.saveAll(assignments);
    }

    @Override
    @Transactional
    public void deleteBuilding(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new InvalidRequestException("Chọn ít nhất một tòa nhà cần xóa.");
        }
        for (Long id : new LinkedHashSet<>(ids)) {
            BuildingEntity building = requireBuilding(id);
            assignmentBuildingRepository.deleteByBuildingId_Id(id);
            listRentAreaRepository.deleteByBuilding_Id(id);
            buildingRepository.delete(building);
        }
    }

    @Override
    @Transactional
    public BuildingDTO insertOrUpdateBuilding(BuildingDTO buildingDTO) {
        normalizeTypeCodes(buildingDTO);
        List<Long> rentAreas = parseRentAreas(buildingDTO.getRentAreas());

        BuildingEntity entity = buildingDTO.getId() == null
                ? new BuildingEntity()
                : requireBuilding(buildingDTO.getId());
        buildingConverter.updateEntity(buildingDTO, entity);
        BuildingEntity savedBuilding = buildingRepository.save(entity);

        if (buildingDTO.getId() != null) {
            listRentAreaRepository.deleteByBuilding_Id(savedBuilding.getId());
            listRentAreaRepository.flush();
        }
        List<RentAreaEntity> savedRentAreas = rentAreas.stream()
                .map(value -> {
                    RentAreaEntity rentArea = new RentAreaEntity();
                    rentArea.setValue(value);
                    rentArea.setBuilding(savedBuilding);
                    return rentArea;
                })
                .collect(Collectors.toList());
        listRentAreaRepository.saveAll(savedRentAreas);

        BuildingDTO result = buildingConverter.toBuildingDTO(savedBuilding);
        result.setRentAreas(rentAreas.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingDTO findBuildingById(Long id) {
        return buildingConverter.toBuildingDTO(requireBuilding(id));
    }

    @Override
    @Transactional(readOnly = true)
    public TypeCodeResponseDTO listTypeCode(Long id) {
        BuildingEntity building = requireBuilding(id);
        Set<String> selectedTypes = StringUtils.isBlank(building.getTypeCode())
                ? Collections.emptySet()
                : new HashSet<>(java.util.Arrays.asList(building.getTypeCode().split(",")));

        List<ResponseDTO> data = new ArrayList<>();
        for (TypeCode typeCode : TypeCode.values()) {
            ResponseDTO type = new ResponseDTO();
            type.setFullName(typeCode.getCode());
            type.setTypeCode(typeCode.name());
            type.setChecked(selectedTypes.contains(typeCode.name()) ? "checked" : "");
            data.add(type);
        }
        TypeCodeResponseDTO result = new TypeCodeResponseDTO();
        result.setData(data);
        return result;
    }

    private BuildingSearchResponse toSearchResponse(BuildingEntity building) {
        BuildingSearchResponse response = new BuildingSearchResponse();
        response.setId(building.getId());
        response.setName(building.getName());
        response.setAddress(joinAddress(building.getStreet(), building.getWard(), building.getDistrict()));
        response.setNumberOfBasement(building.getNumberOfBasement());
        response.setManagerName(building.getManagerName());
        response.setManagerPhoneNumber(building.getManagerPhone());
        response.setFloorArea(building.getFloorArea() == null ? null : building.getFloorArea().longValue());
        response.setRentArea(building.getRentAreas().stream()
                .map(RentAreaEntity::getValue)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        response.setRentPrice(building.getRentPrice() == null ? null : building.getRentPrice().longValue());
        response.setServiceFee(building.getServiceFee());
        response.setBrokerageFee(building.getBrokerageFee() == null ? null : building.getBrokerageFee().doubleValue());
        return response;
    }

    private BuildingEntity requireBuilding(Long id) {
        if (id == null) {
            throw new InvalidRequestException("Mã tòa nhà là bắt buộc.");
        }
        return buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tòa nhà " + id + "."));
    }

    private boolean isActiveStaff(UserEntity user) {
        return user.getStatus() != null && user.getStatus() == 1
                && user.getRoles() != null
                && user.getRoles().stream().anyMatch(role -> "STAFF".equals(role.getCode()));
    }

    private List<Long> parseRentAreas(String rentAreas) {
        if (StringUtils.isBlank(rentAreas)) {
            throw new InvalidRequestException("Diện tích thuê là bắt buộc.");
        }
        LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        for (String rawValue : rentAreas.split(",")) {
            String value = rawValue.trim();
            if (value.isEmpty()) {
                continue;
            }
            try {
                long parsed = Long.parseLong(value);
                if (parsed <= 0) {
                    throw new InvalidRequestException("Diện tích thuê phải lớn hơn 0.");
                }
                normalized.add(parsed);
            } catch (NumberFormatException exception) {
                throw new InvalidRequestException("Diện tích thuê phải là danh sách số, ngăn cách bằng dấu phẩy.");
            }
        }
        if (normalized.isEmpty()) {
            throw new InvalidRequestException("Diện tích thuê là bắt buộc.");
        }
        return new ArrayList<>(normalized);
    }

    private void normalizeTypeCodes(BuildingDTO dto) {
        if (dto.getTypeCode() == null || dto.getTypeCode().isEmpty()) {
            throw new InvalidRequestException("Chọn ít nhất một loại tòa nhà.");
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String rawCode : dto.getTypeCode()) {
            try {
                normalized.add(TypeCode.valueOf(rawCode).name());
            } catch (IllegalArgumentException | NullPointerException exception) {
                throw new InvalidRequestException("Loại tòa nhà không hợp lệ.");
            }
        }
        dto.setTypeCode(new ArrayList<>(normalized));
    }

    private String joinAddress(String street, String ward, String district) {
        return java.util.Arrays.asList(street, ward, district).stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(", "));
    }
}
