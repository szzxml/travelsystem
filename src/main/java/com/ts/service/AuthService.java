package com.ts.service;

import com.ts.dto.LoginRequest;
import com.ts.dto.UserRequest;

import java.util.Map;

public interface AuthService {

    Map<String, Object> login(LoginRequest req);

    Map<String, Object> register(UserRequest req);

    void initAdminIfAbsent();
}
