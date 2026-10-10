package com.javaweb.service.impl;

import com.javaweb.converter.UserConverter;
import com.javaweb.entity.RoleEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.exception.ConflictException;
import com.javaweb.exception.InvalidRequestException;
import com.javaweb.model.dto.PasswordDTO;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.repository.RoleRepository;
import com.javaweb.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserConverter userConverter;
    @InjectMocks
    private UserService userService;

    @Test
    public void filteredListAndCountUseSameActiveUserPredicate() {
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity entity = user(2L, "anna");
        UserDTO converted = new UserDTO();
        converted.setId(2L);
        when(userRepository.searchActiveUsers("ann", 0, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(entity)));
        when(userRepository.countActiveUsers("ann", 0)).thenReturn(1L);
        when(userConverter.convertToDto(entity)).thenReturn(converted);

        List<UserDTO> result = userService.getUsers(" ann ", pageable);
        int total = userService.getTotalItems(" ann ");

        assertThat(result).extracting(UserDTO::getId).containsExactly(2L);
        assertThat(total).isEqualTo(1);
        verify(userRepository).searchActiveUsers("ann", 0, pageable);
        verify(userRepository).countActiveUsers("ann", 0);
    }

    @Test
    public void updateUsesPathIdAndPreservesIdentityFields() {
        UserEntity managed = user(7L, "owner");
        managed.setPassword("encoded-password");
        RoleEntity role = role("STAFF");
        when(userRepository.findById(7L)).thenReturn(Optional.of(managed));
        when(roleRepository.findOneByCode("STAFF")).thenReturn(role);
        when(userRepository.save(managed)).thenReturn(managed);
        when(userConverter.convertToDto(managed)).thenReturn(new UserDTO());
        UserDTO request = validRequest();
        request.setId(99L);
        request.setUserName("attacker");

        userService.update(7L, request);

        assertThat(managed.getId()).isEqualTo(7L);
        assertThat(managed.getUserName()).isEqualTo("owner");
        assertThat(managed.getPassword()).isEqualTo("encoded-password");
        assertThat(managed.getRoles()).containsExactly(role);
        verify(userRepository).save(managed);
    }

    @Test(expected = ConflictException.class)
    public void duplicateUsernameRejectsCreateBeforePersistence() {
        UserDTO request = validRequest();
        when(userRepository.findOneByUserNameIgnoreCase("new.user"))
                .thenReturn(user(8L, "new.user"));

        try {
            userService.insert(request);
        } finally {
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    @Test(expected = InvalidRequestException.class)
    public void unknownRoleRejectsCreateBeforePersistence() {
        UserDTO request = validRequest();
        when(roleRepository.findOneByCode("STAFF")).thenReturn(null);

        try {
            userService.insert(request);
        } finally {
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    @Test(expected = InvalidRequestException.class)
    public void selfDeactivationIsRejectedBeforeAnyWrite() {
        when(userRepository.findOneByUserName("admin")).thenReturn(user(3L, "admin"));

        try {
            userService.delete(new long[]{2L, 3L}, "admin");
        } finally {
            verify(userRepository, never()).saveAll(any(Iterable.class));
            verify(userRepository, never()).findById(any(Long.class));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deleteDeduplicatesIdsAndDeactivatesLoadedUsers() {
        UserEntity current = user(1L, "admin");
        UserEntity first = user(2L, "first");
        UserEntity second = user(3L, "second");
        when(userRepository.findOneByUserName("admin")).thenReturn(current);
        when(userRepository.findById(2L)).thenReturn(Optional.of(first));
        when(userRepository.findById(3L)).thenReturn(Optional.of(second));

        userService.delete(new long[]{2L, 2L, 3L}, "admin");

        ArgumentCaptor<Iterable> saved = ArgumentCaptor.forClass(Iterable.class);
        verify(userRepository).saveAll(saved.capture());
        assertThat(saved.getValue()).containsExactly(first, second);
        assertThat(first.getStatus()).isZero();
        assertThat(second.getStatus()).isZero();
        verify(userRepository).findById(2L);
        verify(userRepository).findById(3L);
    }

    @Test(expected = InvalidRequestException.class)
    public void mismatchedNewPasswordNeverOverwritesExistingHash() {
        UserEntity entity = user(4L, "member");
        entity.setPassword("old-hash");
        when(userRepository.findOneByUserName("member")).thenReturn(entity);
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);
        PasswordDTO request = new PasswordDTO();
        request.setOldPassword("old-password");
        request.setNewPassword("new-password");
        request.setConfirmPassword("different-password");

        try {
            userService.updatePassword("member", request);
        } finally {
            assertThat(entity.getPassword()).isEqualTo("old-hash");
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    private UserDTO validRequest() {
        UserDTO dto = new UserDTO();
        dto.setUserName("new.user");
        dto.setFullName("New User");
        dto.setEmail("new.user@example.com");
        dto.setRoleCode("STAFF");
        dto.setPassword("password-123");
        dto.setConfirmPassword("password-123");
        return dto;
    }

    private UserEntity user(Long id, String username) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setUserName(username);
        entity.setFullName(username);
        entity.setStatus(1);
        entity.setRoles(Arrays.asList(role("STAFF")));
        return entity;
    }

    private RoleEntity role(String code) {
        RoleEntity role = new RoleEntity();
        role.setCode(code);
        role.setName(code);
        return role;
    }
}
