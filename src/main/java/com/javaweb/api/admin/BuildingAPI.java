package com.javaweb.api.admin;

import com.javaweb.model.dto.AssignmentBuildingDTO;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.response.BuildingSearchResponse;
import com.javaweb.model.response.PageResponseDTO;
import com.javaweb.model.response.ResponseDTO;
import com.javaweb.model.response.TypeCodeResponseDTO;
import com.javaweb.service.IBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController("buildingAPIOfAdmin")
@RequestMapping("/api/building")
public class BuildingAPI {

    @Autowired
    private IBuildingService buildingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<PageResponseDTO<BuildingSearchResponse>> searchBuildings(
            @RequestParam Map<String, Object> params,
            @RequestParam(value = "typeCode", required = false) List<String> typeCode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        int pageIndex = Math.max(0, page - 1);
        int pageSize = limit > 0 ? limit : 10;
        int total = buildingService.countTotal(params, typeCode);
        List<BuildingSearchResponse> list = buildingService.findAll(params, typeCode, PageRequest.of(pageIndex, pageSize));
        return ResponseEntity.ok(new PageResponseDTO<>(list, page, pageSize, total));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BuildingDTO addOrUpdateBuilding(@Valid @RequestBody BuildingDTO buildingDTO) {
        return buildingService.insertOrUpdateBuilding(buildingDTO);
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteBuilding(@PathVariable List<Long> ids) {
        buildingService.deleteBuilding(ids);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/assignment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> assignBuilding(@Valid @RequestBody AssignmentBuildingDTO request) {
        buildingService.assignBuilding(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{buildingId}/typeCodes")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public TypeCodeResponseDTO loadTypeCode(@PathVariable("buildingId") Long id) {
        return buildingService.listTypeCode(id);
    }

    @GetMapping("/{id}/staffs")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseDTO loadStaff(@PathVariable Long id) {
        return buildingService.listStaffs(id);
    }
}
