package com.javaweb.repository.custom.impl;

import com.javaweb.entity.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @Mock
    private TypedQuery<UserEntity> typedQuery;

    @InjectMocks
    private UserRepositoryImpl repository;

    @Test
    public void findByRoleBindsRoleCode() {
        String roleCode = "STAFF' OR '1'='1";
        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        repository.findByRole(roleCode);

        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(UserEntity.class));
        assertTrue(jpqlCaptor.getValue().contains("r.code = :roleCode"));
        assertFalse(jpqlCaptor.getValue().contains(roleCode));
        verify(typedQuery).setParameter("roleCode", roleCode);
    }

    @Test
    public void getAllUsersUsesBoundStatusAndJpaPagination() {
        when(entityManager.createNativeQuery(anyString(), eq(UserEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        repository.getAllUsers(PageRequest.of(3, 25));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(sqlCaptor.capture(), eq(UserEntity.class));
        assertTrue(sqlCaptor.getValue().contains("u.status = :activeStatus"));
        assertFalse(sqlCaptor.getValue().toUpperCase().contains(" LIMIT "));
        assertFalse(sqlCaptor.getValue().toUpperCase().contains(" OFFSET "));
        verify(query).setParameter("activeStatus", 1);
        verify(query).setFirstResult(75);
        verify(query).setMaxResults(25);
    }

    @Test
    public void countTotalItemUsesCountQueryAndBoundStatus() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(BigInteger.valueOf(7));

        int count = repository.countTotalItem();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().startsWith("SELECT COUNT(*)"));
        assertTrue(sqlCaptor.getValue().contains("u.status = :activeStatus"));
        verify(query).setParameter("activeStatus", 1);
        assertEquals(7, count);
    }
}
