package com.javaweb.repository.custom.impl;

import com.javaweb.entity.RoleEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private RoleRepositoryImpl repository;

    @Test
    public void findOneByCodeBindsRoleCode() {
        String roleCode = "ADMIN' OR '1'='1";
        RoleEntity expectedRole = new RoleEntity();
        when(entityManager.createNativeQuery(anyString(), eq(RoleEntity.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(expectedRole);

        RoleEntity actualRole = repository.findOneByCode(roleCode);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(sqlCaptor.capture(), eq(RoleEntity.class));
        assertTrue(sqlCaptor.getValue().contains("r.code = :code"));
        assertFalse(sqlCaptor.getValue().contains(roleCode));
        verify(query).setParameter("code", roleCode);
        assertSame(expectedRole, actualRole);
    }
}
