package com.javaweb.model.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

public class AssignmentBuildingDTO {
    private Long id;
    @NotNull(message = "Danh sách nhân viên là bắt buộc")
    private List<Long> staffs ;
    @NotNull(message = "Mã tòa nhà là bắt buộc")
    @Positive(message = "Mã tòa nhà không hợp lệ")
    private Long buildingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Long> staffs) {
        this.staffs = staffs;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }
}
