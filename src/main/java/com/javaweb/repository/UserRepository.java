package com.javaweb.repository;

import com.javaweb.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findOneByUserNameAndStatus(String name, int status);
    List<UserEntity> findByStatusAndRoles_Code(Integer status, String roleCode);
    Page<UserEntity> findByStatusNot(int status, Pageable pageable);
    long countByStatusNot(int status);
    UserEntity findOneByUserName(String userName);
    UserEntity findOneByUserNameIgnoreCase(String userName);
    UserEntity findOneByEmailIgnoreCase(String email);
    List<UserEntity> findByIdIn(List<Long> id);

    @Query("SELECT u FROM UserEntity u WHERE u.status <> :excludedStatus " +
            "AND (LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserEntity> searchActiveUsers(@Param("search") String search,
                                       @Param("excludedStatus") int excludedStatus,
                                       Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.status <> :excludedStatus " +
            "AND (LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    long countActiveUsers(@Param("search") String search,
                          @Param("excludedStatus") int excludedStatus);
}
