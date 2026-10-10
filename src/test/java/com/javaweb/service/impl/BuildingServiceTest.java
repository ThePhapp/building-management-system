package com.javaweb.service.impl;

import com.javaweb.converter.BuildingConverter;
import com.javaweb.converter.BuildingSearchBuilderConverter;
import com.javaweb.entity.AssignmentBuildingEntity;
import com.javaweb.entity.BuildingEntity;
import com.javaweb.entity.RentAreaEntity;
import com.javaweb.entity.RoleEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.exception.InvalidRequestException;
import com.javaweb.exception.ResourceNotFoundException;
import com.javaweb.model.dto.AssignmentBuildingDTO;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.response.ResponseDTO;
import com.javaweb.model.response.StaffResponseDTO;
import com.javaweb.repository.AssignmentBuildingRepository;
import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.ListRentAreaRepository;
import com.javaweb.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuildingServiceTest {
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BuildingSearchBuilderConverter buildingSearchBuilderConverter;
    @Mock
    private ListRentAreaRepository listRentAreaRepository;
    @Mock
    private AssignmentBuildingRepository assignmentBuildingRepository;
    @Spy
    private BuildingConverter buildingConverter = new BuildingConverter();
    @InjectMocks
    private BuildingService buildingService;

    @Test
    public void createSavesBuildingBeforeNormalizedRentAreas() {
        BuildingDTO request = validBuilding();
        request.setRentAreas("100, 200,100");
        when(buildingRepository.save(any(BuildingEntity.class))).thenAnswer(invocation -> {
            BuildingEntity entity = invocation.getArgument(0);
            entity.setId(11L);
            return entity;
        });

        BuildingDTO result = buildingService.insertOrUpdateBuilding(request);

        InOrder order = inOrder(buildingRepository, listRentAreaRepository);
        order.verify(buildingRepository).save(any(BuildingEntity.class));
        order.verify(listRentAreaRepository).saveAll(any(Iterable.class));
        assertThat(result.getId()).isEqualTo(11L);
        assertThat(result.getRentAreas()).isEqualTo("100,200");
        assertThat(result.getTypeCode()).containsExactly("NOI_THAT");
    }

    @Test
    public void updateUsesManagedEntityAndReplacesRentAreas() {
        BuildingEntity managed = new BuildingEntity();
        managed.setId(7L);
        managed.setName("Old name");
        when(buildingRepository.findById(7L)).thenReturn(Optional.of(managed));
        when(buildingRepository.save(managed)).thenReturn(managed);
        BuildingDTO request = validBuilding();
        request.setId(7L);

        BuildingDTO result = buildingService.insertOrUpdateBuilding(request);

        assertThat(managed.getId()).isEqualTo(7L);
        assertThat(managed.getName()).isEqualTo("Tòa nhà A");
        assertThat(result.getId()).isEqualTo(7L);
        InOrder order = inOrder(buildingRepository, listRentAreaRepository);
        order.verify(buildingRepository).save(managed);
        order.verify(listRentAreaRepository).deleteByBuilding_Id(7L);
        order.verify(listRentAreaRepository).flush();
        order.verify(listRentAreaRepository).saveAll(any(Iterable.class));
    }

    @Test
    public void invalidRentAreaStopsBeforePersistence() {
        BuildingDTO request = validBuilding();
        request.setRentAreas("100,not-a-number");

        try {
            buildingService.insertOrUpdateBuilding(request);
        } catch (InvalidRequestException expected) {
            assertThat(expected.getMessage()).contains("danh sách số");
        }

        verify(buildingRepository, never()).save(any(BuildingEntity.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void staffCheckedStateDoesNotDependOnIterationOrder() {
        BuildingEntity building = building(4L);
        UserEntity first = staff(1L, "An");
        UserEntity second = staff(2L, "Bình");
        AssignmentBuildingEntity assignment = new AssignmentBuildingEntity();
        assignment.setBuildingId(building);
        assignment.setStaffId(second);
        when(buildingRepository.findById(4L)).thenReturn(Optional.of(building));
        when(assignmentBuildingRepository.findByBuildingId_Id(4L)).thenReturn(Collections.singletonList(assignment));
        when(userRepository.findByStatusAndRoles_Code(1, "STAFF")).thenReturn(Arrays.asList(first, second));

        ResponseDTO response = buildingService.listStaffs(4L);
        List<StaffResponseDTO> rows = (List<StaffResponseDTO>) response.getData();

        assertThat(rows).extracting(StaffResponseDTO::getChecked).containsExactly("", "checked");
    }

    @Test
    public void assignmentReplacementDeduplicatesStaffIds() {
        BuildingEntity building = building(5L);
        UserEntity first = staff(2L, "An");
        UserEntity second = staff(3L, "Bình");
        when(buildingRepository.findById(5L)).thenReturn(Optional.of(building));
        when(userRepository.findByIdIn(Arrays.asList(2L, 3L))).thenReturn(Arrays.asList(first, second));
        AssignmentBuildingDTO request = new AssignmentBuildingDTO();
        request.setBuildingId(5L);
        request.setStaffs(Arrays.asList(2L, 2L, 3L));

        buildingService.assignBuilding(request);

        verify(assignmentBuildingRepository).deleteByBuildingId_Id(5L);
        verify(assignmentBuildingRepository).flush();
        ArgumentCaptor<Iterable> assignments = ArgumentCaptor.forClass(Iterable.class);
        verify(assignmentBuildingRepository).saveAll(assignments.capture());
        assertThat(assignments.getValue()).hasSize(2);
    }

    @Test
    public void emptyAssignmentListClearsExistingAssignments() {
        BuildingEntity building = building(6L);
        when(buildingRepository.findById(6L)).thenReturn(Optional.of(building));
        AssignmentBuildingDTO request = new AssignmentBuildingDTO();
        request.setBuildingId(6L);
        request.setStaffs(Collections.emptyList());

        buildingService.assignBuilding(request);

        verify(assignmentBuildingRepository).deleteByBuildingId_Id(6L);
        verify(assignmentBuildingRepository).flush();
        verify(assignmentBuildingRepository).saveAll(Collections.emptyList());
        verify(userRepository, never()).findByIdIn(any(List.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void missingBuildingProducesDomainNotFoundError() {
        when(buildingRepository.findById(404L)).thenReturn(Optional.empty());
        buildingService.findBuildingById(404L);
    }

    private BuildingDTO validBuilding() {
        BuildingDTO dto = new BuildingDTO();
        dto.setName("Tòa nhà A");
        dto.setDistrict("QUAN_1");
        dto.setStreet("Nguyễn Huệ");
        dto.setRentAreas("100,200");
        dto.setTypeCode(Collections.singletonList("NOI_THAT"));
        dto.setRentPrice(1000L);
        return dto;
    }

    private BuildingEntity building(Long id) {
        BuildingEntity building = new BuildingEntity();
        building.setId(id);
        return building;
    }

    private UserEntity staff(Long id, String name) {
        RoleEntity role = new RoleEntity();
        role.setCode("STAFF");
        UserEntity staff = new UserEntity();
        staff.setId(id);
        staff.setFullName(name);
        staff.setStatus(1);
        staff.setRoles(Collections.singletonList(role));
        return staff;
    }
}
