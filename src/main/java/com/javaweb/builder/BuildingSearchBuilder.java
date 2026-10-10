package com.javaweb.builder;

import java.util.ArrayList;
import java.util.List;

public class BuildingSearchBuilder {
    private String name;
    private String street;
    private String district;
    private String ward;
    private Long numberOfBasement;
    private String managerName;
    private String managerPhone;
    private Integer rentPriceFrom;
    private Integer rentPriceTo;
    private Integer floorArea;
    private Long rentAreaFrom;
    private Long rentAreaTo;
    private Long staffId;
    private List<String> typeCode = new ArrayList<>();

    private BuildingSearchBuilder(BuildingBuilder builder) {
        this.name = builder.name;
        this.street = builder.street;
        this.district = builder.district;
        this.ward = builder.ward;
        this.numberOfBasement = builder.numberOfBasement;
        this.managerName = builder.managerName;
        this.managerPhone = builder.managerPhone;
        this.rentPriceFrom = builder.rentPriceFrom;
        this.rentPriceTo = builder.rentPriceTo;
        this.floorArea = builder.floorArea;
        this.rentAreaFrom = builder.rentAreaFrom;
        this.rentAreaTo = builder.rentAreaTo;
        this.staffId = builder.staffId;
        this.typeCode = builder.typeCode;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getDistrict() {
        return district;
    }

    public String getWard() {
        return ward;
    }

    public Long getNumberOfBasement() {
        return numberOfBasement;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public Integer getRentPriceFrom() {
        return rentPriceFrom;
    }

    public Integer getRentPriceTo() {
        return rentPriceTo;
    }

    public Integer getFloorArea() {
        return floorArea;
    }

    public Long getRentAreaFrom() {
        return rentAreaFrom;
    }

    public Long getRentAreaTo() {
        return rentAreaTo;
    }

    public Long getStaffId() {
        return staffId;
    }

    public List<String> getTypeCode() {
        return typeCode;
    }

    public static class BuildingBuilder{
        private String name;
        private String street;
        private String district;
        private String ward;
        private Long numberOfBasement;
        private String managerName;
        private String managerPhone;
        private Integer rentPriceFrom;
        private Integer rentPriceTo;
        private Integer floorArea;
        private Long rentAreaFrom;
        private Long rentAreaTo;
        private Long staffId;
        private List<String>typeCode = new ArrayList<>();

        public BuildingBuilder setName(String name) {
            this.name = name;
            return this;
        }
        public BuildingBuilder setStreet(String street) {
            this.street = street;
            return this;
        }
        public BuildingBuilder setDistrict(String district) {
            this.district = district;
            return this;
        }
        public BuildingBuilder setWard(String ward) {
            this.ward = ward;
            return this;
        }
        public BuildingBuilder setNumberOfBasement(Long numberOfBasement) {
            this.numberOfBasement = numberOfBasement;
            return this;
        }
        public BuildingBuilder setManagerName(String managerName) {
            this.managerName = managerName;
            return this;
        }
        public BuildingBuilder setManagerPhoneNumber(String managerPhone) {
            this.managerPhone = managerPhone;
            return this;
        }
        public BuildingBuilder setRentPriceFrom(Integer rentPriceFrom) {
            this.rentPriceFrom = rentPriceFrom;
            return this;
        }
        public BuildingBuilder setFloorArea(Integer floorArea) {
            this.floorArea = floorArea;
            return this;
        }
        public BuildingBuilder setrentPriceTo(Integer rentPriceTo) {
            this.rentPriceTo = rentPriceTo;
            return this;
        }
        public BuildingBuilder setRentAreaFrom(Long rentAreaFrom) {
            this.rentAreaFrom = rentAreaFrom;
            return this;
        }
        public BuildingBuilder setRentAreaTo(Long rentAreaTo) {
            this.rentAreaTo = rentAreaTo;
            return this;
        }
        public BuildingBuilder setStaffId(Long staffId) {
            this.staffId = staffId;
            return this;
        }
        public BuildingBuilder setTypeCode(List<String> typeCode) {
            this.typeCode = typeCode;
            return this;
        }
        public BuildingSearchBuilder build() {
            return new BuildingSearchBuilder(this);
        }
    }
}
