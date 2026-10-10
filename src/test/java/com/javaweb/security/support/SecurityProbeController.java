package com.javaweb.security.support;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test-only endpoints used to exercise the production Spring Security filter
 * chain without loading controllers that depend on persistence services.
 */
@RestController
public class SecurityProbeController {

    @PostMapping("/api/user")
    public ResponseEntity<Void> writeUser() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/building")
    public ResponseEntity<Void> writeBuilding() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/user/list")
    public ResponseEntity<Void> listUsers() {
        return ResponseEntity.ok().build();
    }
}
