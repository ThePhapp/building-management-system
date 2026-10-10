package com.javaweb.api.admin;

import com.javaweb.constant.SystemConstant;
import com.javaweb.model.dto.PasswordDTO;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.model.validation.ValidationGroups;
import com.javaweb.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserAPI {

    @Autowired
    private IUserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUsers(
            @Validated(ValidationGroups.UserCreate.class) @RequestBody UserDTO newUser) {
        return ResponseEntity.ok(userService.insert(newUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUsers(@PathVariable("id") long id,
                                                @Validated(ValidationGroups.UserUpdate.class) @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.update(id, userDTO));
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePasswordUser(Authentication authentication,
                                                       @Validated(ValidationGroups.PasswordChange.class)
                                                       @RequestBody PasswordDTO passwordDTO) {
        userService.updatePassword(authentication.getName(), passwordDTO);
        return ResponseEntity.ok(SystemConstant.UPDATE_SUCCESS);
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> resetPassword(@PathVariable("id") long id,
                                                  @Validated(ValidationGroups.PasswordReset.class)
                                                  @RequestBody PasswordDTO passwordDTO) {
        return ResponseEntity.ok(userService.resetPassword(id, passwordDTO));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateProfileOfUser(Authentication authentication,
                                                        @Validated(ValidationGroups.ProfileUpdate.class)
                                                        @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateProfileOfUser(authentication.getName(), userDTO));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUsers(Authentication authentication, @RequestBody long[] idList) {
        userService.delete(idList, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
