package com.javaweb.repository;

import com.javaweb.entity.AssignmentBuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentBuildingRepository extends JpaRepository<AssignmentBuildingEntity, Long> {
    List<AssignmentBuildingEntity> findByBuildingId_Id(Long buildingId);
    void deleteByBuildingId_Id(Long buildingId);
    boolean existsByBuildingId_IdAndStaffId_Id(Long buildingId, Long staffId);
}
