package com.javaweb.service;

import com.javaweb.model.dto.AssignmentBuildingDTO;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.response.BuildingSearchResponse;
import com.javaweb.model.response.ResponseDTO;
import com.javaweb.model.response.TypeCodeResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IBuildingService {
    List<BuildingSearchResponse> findAll(Map<String, Object> params, List<String> rentType);
    List<BuildingSearchResponse> findAll(Map<String, Object> params, List<String> rentType, Pageable pageable);
    int countTotal(Map<String, Object> params, List<String> rentType);
    ResponseDTO listStaffs(Long id);
    void assignBuilding(AssignmentBuildingDTO request);
    void deleteBuilding(List<Long> ids);
    BuildingDTO insertOrUpdateBuilding(BuildingDTO buildingDTO);
    BuildingDTO findBuildingById(Long id);
    TypeCodeResponseDTO listTypeCode(Long id);
}
