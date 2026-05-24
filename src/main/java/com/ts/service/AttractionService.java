package com.ts.service;

import com.ts.common.PageResult;
import com.ts.dto.AttractionRequest;
import com.ts.entity.Attraction;

public interface AttractionService {

    PageResult<Attraction> list(String keyword, int page, int size);

    Attraction get(Long id);

    Attraction create(AttractionRequest req);

    Attraction update(Long id, AttractionRequest req);

    void delete(Long id);
}
