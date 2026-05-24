package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.dto.LoginRequest;
import com.ts.service.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostConstruct
    public void init() {
        authService.initAdminIfAbsent();
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.login(req));
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody com.ts.dto.UserRequest req) {
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            return ApiResponse.fail(400, "密码不能为空");
        }
        req.setRole("USER");
        return ApiResponse.ok(authService.register(req));
    }
}
