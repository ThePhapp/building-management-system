package com.javaweb.repository;

import com.javaweb.entity.RoleEntity;
import com.javaweb.repository.custom.RoleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>, RoleRepositoryCustom {
}
