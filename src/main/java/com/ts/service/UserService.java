package com.ts.service;

import com.ts.common.PageResult;
import com.ts.dto.UserRequest;
import com.ts.entity.User;

public interface UserService {

    PageResult<User> list(String keyword, int page, int size);

    User get(Long id);

    User create(UserRequest req);

    User update(Long id, UserRequest req);

    void delete(Long id);
}
