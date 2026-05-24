package com.ts.service;

import com.ts.common.PageResult;
import com.ts.dto.RouteRequest;
import com.ts.entity.TourRoute;

public interface RouteService {

    PageResult<TourRoute> list(String keyword, String status, int page, int size);

    TourRoute get(Long id);

    TourRoute create(RouteRequest req);

    TourRoute update(Long id, RouteRequest req);

    void delete(Long id);
}
