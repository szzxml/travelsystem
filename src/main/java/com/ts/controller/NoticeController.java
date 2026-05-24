package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.common.PageResult;
import com.ts.dto.NoticeRequest;
import com.ts.entity.Notice;
import com.ts.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ApiResponse<PageResult<Notice>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(noticeService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Notice> get(@PathVariable Long id) {
        return ApiResponse.ok(noticeService.get(id));
    }

    @PostMapping
    public ApiResponse<Notice> create(@Valid @RequestBody NoticeRequest req) {
        return ApiResponse.ok(noticeService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Notice> update(@PathVariable Long id, @Valid @RequestBody NoticeRequest req) {
        return ApiResponse.ok(noticeService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ApiResponse.ok(null);
    }
}
