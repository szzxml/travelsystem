package com.ts.service;

import com.ts.common.PageResult;
import com.ts.dto.HotelRequest;
import com.ts.entity.Hotel;

public interface HotelService {

    PageResult<Hotel> list(String keyword, String status, int page, int size);

    Hotel get(Long id);

    Hotel create(HotelRequest req);

    Hotel update(Long id, HotelRequest req);

    void delete(Long id);
}
