package com.javaweb.repository.custom.impl;

import com.javaweb.entity.UserEntity;
import com.javaweb.repository.custom.UserRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private static final int ACTIVE_STATUS = 1;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserEntity> findByRole(String roleCode) {
        String jpql = "SELECT DISTINCT u FROM UserEntity u JOIN u.roles r WHERE r.code = :roleCode";
        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        query.setParameter("roleCode", roleCode);
        return query.getResultList();
    }

    @Override
    public List<UserEntity> getAllUsers(Pageable pageable) {
        Query query = entityManager.createNativeQuery(buildQueryFilter(), UserEntity.class);
        query.setParameter("activeStatus", ACTIVE_STATUS);
        query.setFirstResult(toIntOffset(pageable.getOffset()));
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

    @Override
    public int countTotalItem() {
        String sql = "SELECT COUNT(*) FROM user u WHERE u.status = :activeStatus";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("activeStatus", ACTIVE_STATUS);
        return ((Number) query.getSingleResult()).intValue();
    }

    private String buildQueryFilter() {
        return "SELECT u.* FROM user u WHERE u.status = :activeStatus";
    }

    private int toIntOffset(long offset) {
        if (offset > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Page offset exceeds the supported JPA range");
        }
        return (int) offset;
    }
}
