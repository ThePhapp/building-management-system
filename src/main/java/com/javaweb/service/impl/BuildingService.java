package com.javaweb.service.impl;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.converter.BuildingConverter;
import com.javaweb.converter.BuildingSearchBuilderConverter;
import com.javaweb.entity.AssignmentBuildingEntity;
import com.javaweb.entity.BuildingEntity;
import com.javaweb.entity.RentAreaEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.enums.TypeCode;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private ModelMapper modelMapper;
    @Autowired
    private BuildingConverter buildingConverter;

    @Override
    public List<BuildingSearchResponse> findAll(Map<String, Object> param, List<String> typeCode) {
        BuildingSearchBuilder builder = buildingSearchBuilderConverter.toBuildingSearchBuilder(param, typeCode);
        List<BuildingEntity> buildingEntities = buildingRepository.findAll(builder);
        List<BuildingSearchResponse> result = new ArrayList<>();
        for (BuildingEntity it : buildingEntities) {
            BuildingSearchResponse buildingSearchResponse = new BuildingSearchResponse();
            buildingSearchResponse.setId(it.getId());
            buildingSearchResponse.setName(it.getName());
            result.add(buildingSearchResponse);
        }
        return result;
    }

    @Override
    public ResponseDTO listStaffs(Long id) {
        List<UserEntity> staffs = userRepository.findByStatusAndRoles_Code(1, "STAFF");
        BuildingEntity buildingEntity = buildingRepository.findById(id).get();
        if (buildingEntity == null) {
            System.out.println("Building not found");
        }
        List<StaffResponseDTO> responseDTOS = new ArrayList<>();
        List<AssignmentBuildingEntity> assignmentBuildingEntities = buildingEntity.getBuildingId();
        ResponseDTO result = new ResponseDTO();

        for (UserEntity it : staffs) {
            StaffResponseDTO staffDTO = new StaffResponseDTO();
            staffDTO.setFullName(it.getFullName());
            staffDTO.setStaffId(it.getId());
            for (AssignmentBuildingEntity it1 : assignmentBuildingEntities) {
                Long staffId = it1.getStaffId().getId();
                if (staffId.equals(it.getId())) {
                    staffDTO.setChecked("checked");
                } else {
                    staffDTO.setChecked("");
                }
            }
            responseDTOS.add(staffDTO);
        }
        result.setData(responseDTOS);
        result.setMessage("Thành công");

        return result;
    }

    @Override
    public String listRentArea(Long id) {
        BuildingEntity building = buildingRepository.findById(id).get();
        List<RentAreaEntity> rentArea = building.getRentAreas();
        //java-8
        String listRentArea = rentArea.stream().map(it -> it.getValue().toString()).collect(Collectors.joining(","));
        return listRentArea;
    }

    @Override
    @Transactional
    public void deleteBuilding(List<Long> ids) {
        for(Long list : ids) {
           List<RentAreaEntity> rentAreaEntities = listRentAreaRepository.findByBuilding_Id(list);
            for (RentAreaEntity it : rentAreaEntities) {
                listRentAreaRepository.deleteById(it.getId());
            }
            List<AssignmentBuildingEntity> results = assignmentBuildingRepository.findAll();
           for (AssignmentBuildingEntity it : results) {
               if (it.getBuildingId().getId().equals(list)) {
                   assignmentBuildingRepository.deleteById(it.getId());
               }
           }
              buildingRepository.deleteById(list);
        }
    }

    @Override
    @Transactional
    public void saveBuilding(BuildingEntity buildingEntity) {
        buildingRepository.save(buildingEntity);
    }

    @Override
    @Transactional
    public BuildingDTO insertOrUpdateBuilding(BuildingDTO buildingDTO) {
        BuildingEntity result = modelMapper.map(buildingDTO, BuildingEntity.class);
        String[] list = buildingDTO.getRentAreas().split(",");
        if(result.getId()!=null){
            List<RentAreaEntity> listRentArea = listRentAreaRepository.findByBuilding_Id(buildingDTO.getId());
            for (RentAreaEntity item1 : listRentArea) {
                listRentAreaRepository.deleteById(item1.getId());
            }
        }
        for (String it : list) {
            RentAreaEntity rentAreaEntity = new RentAreaEntity();
            rentAreaEntity.setValue(Long.parseLong(it));
            rentAreaEntity.setBuilding(result);
            listRentAreaRepository.save(rentAreaEntity);
        }
        return buildingConverter.toBuildingDTO(buildingRepository.save(result));
    }


    @Override
    public BuildingEntity findById(Long id) {
        return buildingRepository.findById(id).get();
    }

    @Override
    public TypeCodeResponseDTO listTypeCode(Long id) {
        BuildingEntity building = buildingRepository.findById(id).get();
        List<ResponseDTO> list = new ArrayList<>();
        TypeCodeResponseDTO typeCodeResponseDTO = new TypeCodeResponseDTO();

        if (building.getTypeCode() != null) {
            for (TypeCode it : TypeCode.values()) {
                ResponseDTO typeDTO = new ResponseDTO();
                typeDTO.setFullName(it.getCode());
                typeDTO.setTypeCode(it.name());
                typeDTO.setChecked("");
                list.add(typeDTO);
            }
        } else {
            String[] buildingType = building.getTypeCode().split(",");
            for (TypeCode it : TypeCode.values()) {
                ResponseDTO typeDTO = new ResponseDTO();
                typeDTO.setFullName(it.getCode());
                typeDTO.setTypeCode(it.name());
                for (String it1 : buildingType) {
                    if (it1.equals(it.name())) {
                        typeDTO.setChecked("checked");
                        break;
                    } else {
                        typeDTO.setChecked("");
                    }
                }
                list.add(typeDTO);
            }
        }
        typeCodeResponseDTO.setData(list);
        return typeCodeResponseDTO;
    }
}
