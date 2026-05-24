package com.ts.service;

import com.ts.common.PageResult;
import com.ts.entity.Attraction;
import com.ts.entity.Notice;
import com.ts.entity.TourRoute;

public interface PublicService {

    PageResult<TourRoute> routes(String keyword, int page, int size);

    TourRoute routeDetail(Long id);

    PageResult<Attraction> attractions(String keyword, int page, int size);

    Attraction attractionDetail(Long id);

    PageResult<Notice> notices(int page, int size);
}
