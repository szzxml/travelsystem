package com.ts.controller;

import com.ts.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/health")
    public ApiResponse<String> health() {
        return ApiResponse.ok("ok");
    }
}
