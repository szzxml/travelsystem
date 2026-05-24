package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.common.PageResult;
import com.ts.dto.HotelRequest;
import com.ts.entity.Hotel;
import com.ts.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ApiResponse<PageResult<Hotel>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(hotelService.list(keyword, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Hotel> get(@PathVariable Long id) {
        return ApiResponse.ok(hotelService.get(id));
    }

    @PostMapping
    public ApiResponse<Hotel> create(@Valid @RequestBody HotelRequest req) {
        return ApiResponse.ok(hotelService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Hotel> update(@PathVariable Long id, @Valid @RequestBody HotelRequest req) {
        return ApiResponse.ok(hotelService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ApiResponse.ok(null);
    }
}
