package com.javaweb.repository.custom.impl;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.entity.BuildingEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuildingRepositoryImplTest {

    private static final String INJECTION_VALUE = "Tower' OR 1=1 --";

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private BuildingRepositoryImpl repository;

    @Before
    public void setUp() {
        when(entityManager.createNativeQuery(anyString(), eq(BuildingEntity.class))).thenReturn(query);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());
        when(query.getSingleResult()).thenReturn(10L);
    }

    @Test
    public void findAllBindsEverySearchValue() {
        BuildingSearchBuilder builder = new BuildingSearchBuilder.BuildingBuilder()
                .setName(INJECTION_VALUE)
                .setStreet("Main_%")
                .setDistrict("DISTRICT_1")
                .setWard("Ward 4")
                .setNumberOfBasement(2L)
                .setManagerName("Manager")
                .setManagerPhoneNumber("0900000000")
                .setFloorArea(250)
                .setRentPriceFrom(1000)
                .setrentPriceTo(5000)
                .setRentAreaFrom(50L)
                .setRentAreaTo(200L)
                .setStaffId(9L)
                .setDirection("South")
                .setLevel("Grade A")
                .setTypeCode(Arrays.asList("OFFICE", INJECTION_VALUE))
                .build();

        repository.findAll(builder);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(sqlCaptor.capture(), eq(BuildingEntity.class));
        String sql = sqlCaptor.getValue();

        assertFalse(sql.contains(INJECTION_VALUE));
        assertTrue(sql.contains("b.name LIKE :name"));
        assertTrue(sql.contains("b.numberofbasement = :numberOfBasement"));
        assertTrue(sql.contains("b.rentprice >= :rentPriceFrom"));
        assertTrue(sql.contains("r.value <= :rentAreaTo"));
        assertTrue(sql.contains("ab.staffid = :staffId"));
        assertTrue(sql.contains("b.direction LIKE :direction"));
        assertTrue(sql.contains("b.level LIKE :level"));
        assertTrue(sql.contains("b.type LIKE :typeCode0 OR b.type LIKE :typeCode1"));

        verify(query).setParameter("name", "%" + INJECTION_VALUE + "%");
        verify(query).setParameter("street", "%Main_%%");
        verify(query).setParameter("district", "%DISTRICT_1%");
        verify(query).setParameter("ward", "%Ward 4%");
        verify(query).setParameter("numberOfBasement", 2L);
        verify(query).setParameter("managerName", "%Manager%");
        verify(query).setParameter("managerPhone", "%0900000000%");
        verify(query).setParameter("floorArea", 250);
        verify(query).setParameter("rentPriceFrom", 1000);
        verify(query).setParameter("rentPriceTo", 5000);
        verify(query).setParameter("rentAreaFrom", 50L);
        verify(query).setParameter("rentAreaTo", 200L);
        verify(query).setParameter("staffId", 9L);
        verify(query).setParameter("direction", "%South%");
        verify(query).setParameter("level", "%Grade A%");
        verify(query).setParameter("typeCode0", "%OFFICE%");
        verify(query).setParameter("typeCode1", "%" + INJECTION_VALUE + "%");
    }

    @Test
    public void findAllWithPaginationAppliesOffsetAndLimit() {
        BuildingSearchBuilder builder = new BuildingSearchBuilder.BuildingBuilder()
                .setName("Bitexco")
                .build();

        repository.findAll(builder, PageRequest.of(2, 5));

        verify(query).setFirstResult(10);
        verify(query).setMaxResults(5);
    }

    @Test
    public void countTotalReturnsCountNumber() {
        BuildingSearchBuilder builder = new BuildingSearchBuilder.BuildingBuilder()
                .setName("Landmark")
                .build();

        int total = repository.countTotal(builder);
        assertEquals(10, total);
    }
}
