package com.ts.service;

import com.ts.common.PageResult;
import com.ts.dto.NoticeRequest;
import com.ts.entity.Notice;

public interface NoticeService {

    PageResult<Notice> list(String keyword, int page, int size);

    Notice get(Long id);

    Notice create(NoticeRequest req);

    Notice update(Long id, NoticeRequest req);

    void delete(Long id);
}
