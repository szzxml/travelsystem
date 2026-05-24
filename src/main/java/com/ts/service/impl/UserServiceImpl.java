package com.ts.service.impl;

import com.ts.common.PageResult;
import com.ts.dto.UserRequest;
import com.ts.entity.User;
import com.ts.exception.BusinessException;
import com.ts.repository.UserRepository;
import com.ts.service.UserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<User> list(String keyword, int page, int size) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.or(
                        cb.like(root.get("username"), "%" + keyword + "%"),
                        cb.like(root.get("realName"), "%" + keyword + "%")
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> result = userRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User create(UserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        User user = new User();
        applyRequest(user, req, true);
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User update(Long id, UserRequest req) {
        User user = get(id);
        applyRequest(user, req, false);
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void applyRequest(User user, UserRequest req, boolean isCreate) {
        if (isCreate) {
            user.setUsername(req.getUsername());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getRealName() != null) {
            user.setRealName(req.getRealName());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        if (req.getRole() != null) {
            user.setRole(User.Role.valueOf(req.getRole()));
        }
        if (req.getEnabled() != null) {
            user.setEnabled(req.getEnabled());
        }
    }
}
