package com.javaweb.repository.custom.impl;

import com.javaweb.entity.RoleEntity;
import com.javaweb.repository.custom.RoleRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RoleEntity findOneByCode(String code) {
        String sql = "SELECT r.* FROM role r WHERE r.code = :code";
        Query query = entityManager.createNativeQuery(sql, RoleEntity.class);
        query.setParameter("code", code);
        return (RoleEntity) query.getSingleResult();
    }
}
