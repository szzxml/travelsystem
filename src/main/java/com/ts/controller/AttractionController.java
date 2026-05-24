package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.common.PageResult;
import com.ts.dto.AttractionRequest;
import com.ts.entity.Attraction;
import com.ts.service.AttractionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/attractions")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping
    public ApiResponse<PageResult<Attraction>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(attractionService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Attraction> get(@PathVariable Long id) {
        return ApiResponse.ok(attractionService.get(id));
    }

    @PostMapping
    public ApiResponse<Attraction> create(@Valid @RequestBody AttractionRequest req) {
        return ApiResponse.ok(attractionService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Attraction> update(@PathVariable Long id, @Valid @RequestBody AttractionRequest req) {
        return ApiResponse.ok(attractionService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        attractionService.delete(id);
        return ApiResponse.ok(null);
    }
}
