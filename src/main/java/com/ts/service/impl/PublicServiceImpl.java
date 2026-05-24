package com.ts.service.impl;

import com.ts.common.PageResult;
import com.ts.entity.Attraction;
import com.ts.entity.Notice;
import com.ts.entity.TourRoute;
import com.ts.exception.BusinessException;
import com.ts.repository.AttractionRepository;
import com.ts.repository.NoticeRepository;
import com.ts.repository.TourRouteRepository;
import com.ts.service.PublicService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublicServiceImpl implements PublicService {

    private final TourRouteRepository routeRepository;
    private final AttractionRepository attractionRepository;
    private final NoticeRepository noticeRepository;

    public PublicServiceImpl(TourRouteRepository routeRepository,
                             AttractionRepository attractionRepository,
                             NoticeRepository noticeRepository) {
        this.routeRepository = routeRepository;
        this.attractionRepository = attractionRepository;
        this.noticeRepository = noticeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<TourRoute> routes(String keyword, int page, int size) {
        Specification<TourRoute> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), TourRoute.Status.PUBLISHED));
            if (!keyword.isBlank()) {
                predicates.add(cb.or(
                        cb.like(root.get("title"), "%" + keyword + "%"),
                        cb.like(root.get("destination"), "%" + keyword + "%")
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TourRoute> result = routeRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public TourRoute routeDetail(Long id) {
        return routeRepository.findById(id).orElseThrow(() -> new BusinessException(404, "线路不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Attraction> attractions(String keyword, int page, int size) {
        Specification<Attraction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), Attraction.Status.OPEN));
            if (!keyword.isBlank()) {
                predicates.add(cb.or(
                        cb.like(root.get("name"), "%" + keyword + "%"),
                        cb.like(root.get("location"), "%" + keyword + "%")
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Attraction> result = attractionRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Attraction attractionDetail(Long id) {
        return attractionRepository.findById(id).orElseThrow(() -> new BusinessException(404, "景点不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Notice> notices(int page, int size) {
        Specification<Notice> spec = (root, query, cb) -> cb.isTrue(root.get("published"));
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notice> result = noticeRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }
}
