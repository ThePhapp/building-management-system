package com.javaweb.service.impl;

import com.javaweb.constant.SystemConstant;
import com.javaweb.converter.UserConverter;
import com.javaweb.entity.RoleEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.exception.ConflictException;
import com.javaweb.exception.InvalidRequestException;
import com.javaweb.exception.ResourceNotFoundException;
import com.javaweb.model.dto.PasswordDTO;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.repository.RoleRepository;
import com.javaweb.repository.UserRepository;
import com.javaweb.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements IUserService {
    private static final int DISABLED_STATUS = 0;
    private static final int ACTIVE_STATUS = 1;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{3,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserConverter userConverter;

    @Override
    @Transactional(readOnly = true)
    public UserDTO findOneByUserNameAndStatus(String name, int status) {
        UserEntity entity = userRepository.findOneByUserNameAndStatus(name, status);
        return entity == null ? null : userConverter.convertToDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsers(String searchValue, Pageable pageable) {
        Page<UserEntity> users = StringUtils.isNotBlank(searchValue)
                ? userRepository.searchActiveUsers(searchValue.trim(), DISABLED_STATUS, pageable)
                : userRepository.findByStatusNot(DISABLED_STATUS, pageable);
        return users.getContent().stream().map(this::toListDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalItems(String searchValue) {
        long count = StringUtils.isNotBlank(searchValue)
                ? userRepository.countActiveUsers(searchValue.trim(), DISABLED_STATUS)
                : userRepository.countByStatusNot(DISABLED_STATUS);
        if (count > Integer.MAX_VALUE) {
            throw new IllegalStateException("User count exceeds supported pagination range");
        }
        return (int) count;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findOneByUserName(String userName) {
        return userConverter.convertToDto(requireUserByUsername(userName));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findUserById(long id) {
        return toListDto(requireUser(id));
    }

    @Override
    @Transactional
    public UserDTO insert(UserDTO newUser) {
        validateUsername(newUser.getUserName());
        validateFullName(newUser.getFullName());
        validateEmail(newUser.getEmail());
        validateNewPassword(newUser.getPassword(), newUser.getConfirmPassword());
        ensureUsernameAvailable(newUser.getUserName(), null);
        ensureEmailAvailable(newUser.getEmail(), null);

        RoleEntity role = requireRole(newUser.getRoleCode());
        UserEntity entity = userConverter.convertToEntity(newUser);
        entity.setId(null);
        entity.setUserName(newUser.getUserName().trim());
        entity.setFullName(newUser.getFullName().trim());
        entity.setEmail(normalizeOptional(newUser.getEmail()));
        entity.setRoles(Stream.of(role).collect(Collectors.toList()));
        entity.setStatus(ACTIVE_STATUS);
        entity.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userConverter.convertToDto(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserDTO update(Long id, UserDTO updateUser) {
        UserEntity entity = requireUser(id);
        validateFullName(updateUser.getFullName());
        validateEmail(updateUser.getEmail());
        RoleEntity role = requireRole(updateUser.getRoleCode());
        ensureEmailAvailable(updateUser.getEmail(), id);

        entity.setFullName(updateUser.getFullName().trim());
        entity.setEmail(normalizeOptional(updateUser.getEmail()));
        entity.setRoles(Stream.of(role).collect(Collectors.toList()));
        return userConverter.convertToDto(userRepository.save(entity));
    }

    @Override
    @Transactional
    public void updatePassword(String username, PasswordDTO passwordDTO) {
        UserEntity user = requireUserByUsername(username);
        if (passwordDTO == null || StringUtils.isBlank(passwordDTO.getOldPassword())
                || !passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException(SystemConstant.CHANGE_PASSWORD_FAIL);
        }
        validateNewPassword(passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword());
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO resetPassword(long id, PasswordDTO passwordDTO) {
        validateNewPassword(passwordDTO == null ? null : passwordDTO.getNewPassword(),
                passwordDTO == null ? null : passwordDTO.getConfirmPassword());
        UserEntity entity = requireUser(id);
        entity.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        return userConverter.convertToDto(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserDTO updateProfileOfUser(String username, UserDTO updateUser) {
        UserEntity entity = requireUserByUsername(username);
        validateFullName(updateUser.getFullName());
        validateEmail(updateUser.getEmail());
        ensureEmailAvailable(updateUser.getEmail(), entity.getId());
        entity.setFullName(updateUser.getFullName().trim());
        entity.setEmail(normalizeOptional(updateUser.getEmail()));
        return userConverter.convertToDto(userRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(long[] ids, String currentUsername) {
        if (ids == null || ids.length == 0) {
            throw new InvalidRequestException("Chọn ít nhất một người dùng cần vô hiệu hóa.");
        }
        UserEntity currentUser = requireUserByUsername(currentUsername);
        LinkedHashSet<Long> uniqueIds = new LinkedHashSet<>();
        for (long id : ids) {
            uniqueIds.add(id);
        }
        if (uniqueIds.contains(currentUser.getId())) {
            throw new InvalidRequestException("Bạn không thể vô hiệu hóa tài khoản đang đăng nhập.");
        }

        List<UserEntity> users = new ArrayList<>();
        for (Long id : uniqueIds) {
            users.add(requireUser(id));
        }
        users.forEach(user -> user.setStatus(DISABLED_STATUS));
        userRepository.saveAll(users);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getStaffs() {
        Map<Long, String> result = new HashMap<>();
        userRepository.findByStatusAndRoles_Code(ACTIVE_STATUS, "STAFF")
                .forEach(user -> result.put(user.getId(), user.getFullName()));
        return result;
    }

    private UserDTO toListDto(UserEntity entity) {
        UserDTO dto = userConverter.convertToDto(entity);
        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            dto.setRoleCode(entity.getRoles().get(0).getCode());
            dto.setRoleName(entity.getRoles().get(0).getName());
        }
        return dto;
    }

    private UserEntity requireUser(Long id) {
        if (id == null) {
            throw new InvalidRequestException("Mã người dùng là bắt buộc.");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng " + id + "."));
    }

    private UserEntity requireUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new InvalidRequestException("Tên đăng nhập là bắt buộc.");
        }
        UserEntity entity = userRepository.findOneByUserName(username);
        if (entity == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng.");
        }
        return entity;
    }

    private RoleEntity requireRole(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            throw new InvalidRequestException("Vai trò là bắt buộc.");
        }
        RoleEntity role = roleRepository.findOneByCode(roleCode.trim().toUpperCase());
        if (role == null) {
            throw new InvalidRequestException("Vai trò không hợp lệ.");
        }
        return role;
    }

    private void ensureUsernameAvailable(String username, Long currentId) {
        UserEntity existing = userRepository.findOneByUserNameIgnoreCase(username.trim());
        if (existing != null && (currentId == null || !currentId.equals(existing.getId()))) {
            throw new ConflictException("Tên đăng nhập đã tồn tại.");
        }
    }

    private void ensureEmailAvailable(String email, Long currentId) {
        if (StringUtils.isBlank(email)) {
            return;
        }
        UserEntity existing = userRepository.findOneByEmailIgnoreCase(email.trim());
        if (existing != null && (currentId == null || !currentId.equals(existing.getId()))) {
            throw new ConflictException("Email đã tồn tại.");
        }
    }

    private void validateUsername(String username) {
        if (StringUtils.isBlank(username) || !USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new InvalidRequestException("Tên đăng nhập dài 3-50 ký tự và chỉ gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.");
        }
    }

    private void validateFullName(String fullName) {
        if (StringUtils.isBlank(fullName) || fullName.trim().length() > 255) {
            throw new InvalidRequestException("Tên đầy đủ là bắt buộc và tối đa 255 ký tự.");
        }
    }

    private void validateNewPassword(String password, String confirmation) {
        if (StringUtils.isBlank(password) || password.length() < 8 || !password.equals(confirmation)) {
            throw new InvalidRequestException(SystemConstant.PASSWORD_POLICY_FAIL);
        }
    }

    private void validateEmail(String email) {
        if (StringUtils.isNotBlank(email) && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new InvalidRequestException("Email không hợp lệ.");
        }
    }

    private String normalizeOptional(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
