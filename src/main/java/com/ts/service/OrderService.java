package com.ts.service;

import com.ts.common.PageResult;
import com.ts.dto.OrderRequest;
import com.ts.entity.Order;

public interface OrderService {

    PageResult<Order> list(String keyword, String status, int page, int size);

    PageResult<Order> listByUser(String username, int page, int size);

    Order get(Long id);

    Order create(String username, OrderRequest req);

    Order updateStatus(Long id, String status, String rejectReason);

    void delete(Long id);
}
