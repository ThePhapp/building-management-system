package com.javaweb.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaweb.model.validation.ValidationGroups;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDTO extends AbstractDTO {
    @NotBlank(groups = ValidationGroups.UserCreate.class, message = "Tên đăng nhập là bắt buộc")
    @Pattern(groups = ValidationGroups.UserCreate.class, regexp = "^[A-Za-z0-9._-]{3,50}$",
            message = "Tên đăng nhập dài 3-50 ký tự và chỉ gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang")
    private String userName;
    @NotBlank(groups = {ValidationGroups.UserCreate.class, ValidationGroups.UserUpdate.class,
            ValidationGroups.ProfileUpdate.class}, message = "Tên đầy đủ là bắt buộc")
    @Size(groups = {ValidationGroups.UserCreate.class, ValidationGroups.UserUpdate.class,
            ValidationGroups.ProfileUpdate.class}, max = 255, message = "Tên đầy đủ tối đa 255 ký tự")
    private String fullName;
    @Email(groups = {ValidationGroups.UserCreate.class, ValidationGroups.UserUpdate.class,
            ValidationGroups.ProfileUpdate.class}, message = "Email không hợp lệ")
    @Size(groups = {ValidationGroups.UserCreate.class, ValidationGroups.UserUpdate.class,
            ValidationGroups.ProfileUpdate.class}, max = 255, message = "Email tối đa 255 ký tự")
    private String email;
    @NotBlank(groups = ValidationGroups.UserCreate.class, message = "Mật khẩu là bắt buộc")
    @Size(groups = ValidationGroups.UserCreate.class, min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;
    @NotBlank(groups = ValidationGroups.UserCreate.class, message = "Xác nhận mật khẩu là bắt buộc")
    private String confirmPassword;
    private Integer status;
    private List<RoleDTO> roles = new ArrayList<>();
    private String roleName;
    @NotBlank(groups = {ValidationGroups.UserCreate.class, ValidationGroups.UserUpdate.class},
            message = "Vai trò là bắt buộc")
    private String roleCode;
    private Map<String,String> roleDTOs = new HashMap<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Map<String, String> getRoleDTOs() {
        return roleDTOs;
    }

    public void setRoleDTOs(Map<String, String> roleDTOs) {
        this.roleDTOs = roleDTOs;
    }
}
