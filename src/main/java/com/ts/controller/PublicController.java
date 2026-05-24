package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.common.PageResult;
import com.ts.entity.Attraction;
import com.ts.entity.Notice;
import com.ts.entity.TourRoute;
import com.ts.service.PublicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/routes")
    public ApiResponse<PageResult<TourRoute>> routes(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        return ApiResponse.ok(publicService.routes(keyword, page, size));
    }

    @GetMapping("/routes/{id}")
    public ApiResponse<TourRoute> routeDetail(@PathVariable Long id) {
        return ApiResponse.ok(publicService.routeDetail(id));
    }

    @GetMapping("/attractions")
    public ApiResponse<PageResult<Attraction>> attractions(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        return ApiResponse.ok(publicService.attractions(keyword, page, size));
    }

    @GetMapping("/attractions/{id}")
    public ApiResponse<Attraction> attractionDetail(@PathVariable Long id) {
        return ApiResponse.ok(publicService.attractionDetail(id));
    }

    @GetMapping("/notices")
    public ApiResponse<PageResult<Notice>> notices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.ok(publicService.notices(page, size));
    }
}
