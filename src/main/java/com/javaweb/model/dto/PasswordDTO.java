package com.javaweb.model.dto;

import com.javaweb.model.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordDTO extends AbstractDTO<PasswordDTO> {

    private static final long serialVersionUID = 8835146939192307340L;

    @NotBlank(groups = ValidationGroups.PasswordChange.class, message = "Mật khẩu hiện tại là bắt buộc")
    private String oldPassword;
    @NotBlank(groups = {ValidationGroups.PasswordChange.class, ValidationGroups.PasswordReset.class},
            message = "Mật khẩu mới là bắt buộc")
    @Size(groups = {ValidationGroups.PasswordChange.class, ValidationGroups.PasswordReset.class}, min = 8,
            message = "Mật khẩu mới phải có ít nhất 8 ký tự")
    private String newPassword;
    @NotBlank(groups = {ValidationGroups.PasswordChange.class, ValidationGroups.PasswordReset.class},
            message = "Xác nhận mật khẩu là bắt buộc")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
