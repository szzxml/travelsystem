package com.ts.service.impl;

import com.ts.dto.LoginRequest;
import com.ts.dto.UserRequest;
import com.ts.entity.User;
import com.ts.exception.BusinessException;
import com.ts.repository.UserRepository;
import com.ts.security.JwtUtil;
import com.ts.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException(401, "账号或密码错误"));
        if (!user.getEnabled()) {
            throw new BusinessException(403, "账号已被禁用");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "账号或密码错误");
        }
        String token = jwtUtil.generate(user.getUsername(), user.getRole().name());
        return buildAuthPayload(user, token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> register(UserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setRole(User.Role.USER);
        userRepository.save(user);
        String token = jwtUtil.generate(user.getUsername(), user.getRole().name());
        return buildAuthPayload(user, token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initAdminIfAbsent() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("超级管理员");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }
    }

    private Map<String, Object> buildAuthPayload(User user, String token) {
        return Map.of(
                "token", token,
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "realName", user.getRealName() != null ? user.getRealName() : user.getUsername()
        );
    }
}
