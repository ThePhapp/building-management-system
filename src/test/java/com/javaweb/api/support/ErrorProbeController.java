package com.javaweb.api.support;

import com.javaweb.exception.ConflictException;
import com.javaweb.exception.ResourceNotFoundException;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.model.validation.ValidationGroups;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/error-probe")
public class ErrorProbeController {
    @PostMapping("/body")
    public void validateBody(@Valid @RequestBody BuildingDTO body) {
    }

    @PostMapping("/user-create")
    public void validateUserCreate(
            @Validated(ValidationGroups.UserCreate.class) @RequestBody UserDTO body) {
    }

    @GetMapping("/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("Không tìm thấy bản ghi.");
    }

    @GetMapping("/conflict")
    public void conflict() {
        throw new ConflictException("Dữ liệu đã tồn tại.");
    }

    @GetMapping("/unexpected")
    public void unexpected() {
        throw new IllegalStateException("database-password-secret");
    }
}
