package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.common.PageResult;
import com.ts.dto.OrderRequest;
import com.ts.entity.Order;
import com.ts.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 用户下单
    @PostMapping("/api/orders")
    public ApiResponse<Order> create(@Valid @RequestBody OrderRequest req, Authentication auth) {
        return ApiResponse.ok(orderService.create(auth.getName(), req));
    }

    // 用户查看自己的订单
    @GetMapping("/api/orders")
    public ApiResponse<PageResult<Order>> myOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        return ApiResponse.ok(orderService.listByUser(auth.getName(), page, size));
    }

    // 管理员接口
    @GetMapping("/api/admin/orders")
    public ApiResponse<PageResult<Order>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(orderService.list(keyword, status, page, size));
    }

    @GetMapping("/api/admin/orders/{id}")
    public ApiResponse<Order> get(@PathVariable Long id) {
        return ApiResponse.ok(orderService.get(id));
    }

    @PatchMapping("/api/admin/orders/{id}/status")
    public ApiResponse<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String rejectReason) {
        return ApiResponse.ok(orderService.updateStatus(id, status, rejectReason));
    }

    @DeleteMapping("/api/admin/orders/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ApiResponse.ok(null);
    }
}
