package com.javaweb.repository.custom;

import com.javaweb.entity.RoleEntity;

public interface RoleRepositoryCustom {
    RoleEntity findOneByCode(String code);
}
