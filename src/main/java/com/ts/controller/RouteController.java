package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.common.PageResult;
import com.ts.dto.RouteRequest;
import com.ts.entity.TourRoute;
import com.ts.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ApiResponse<PageResult<TourRoute>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(routeService.list(keyword, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<TourRoute> get(@PathVariable Long id) {
        return ApiResponse.ok(routeService.get(id));
    }

    @PostMapping
    public ApiResponse<TourRoute> create(@Valid @RequestBody RouteRequest req) {
        return ApiResponse.ok(routeService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<TourRoute> update(@PathVariable Long id, @Valid @RequestBody RouteRequest req) {
        return ApiResponse.ok(routeService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ApiResponse.ok(null);
    }
}
