package com.ts.service.impl;

import com.ts.common.PageResult;
import com.ts.dto.RouteRequest;
import com.ts.entity.Hotel;
import com.ts.entity.TourRoute;
import com.ts.exception.BusinessException;
import com.ts.repository.HotelRepository;
import com.ts.repository.TourRouteRepository;
import com.ts.service.RouteService;
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
public class RouteServiceImpl implements RouteService {

    private final TourRouteRepository routeRepository;
    private final HotelRepository hotelRepository;

    public RouteServiceImpl(TourRouteRepository routeRepository, HotelRepository hotelRepository) {
        this.routeRepository = routeRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<TourRoute> list(String keyword, String status, int page, int size) {
        Specification<TourRoute> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("title"), "%" + keyword + "%"));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), TourRoute.Status.valueOf(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TourRoute> result = routeRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public TourRoute get(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Route not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TourRoute create(RouteRequest req) {
        TourRoute route = new TourRoute();
        applyRequest(route, req);
        return routeRepository.save(route);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TourRoute update(Long id, RouteRequest req) {
        TourRoute route = get(id);
        applyRequest(route, req);
        return routeRepository.save(route);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }

    private void applyRequest(TourRoute route, RouteRequest req) {
        route.setTitle(req.getTitle());
        route.setDescription(req.getDescription());
        route.setCoverImage(req.getCoverImage());
        route.setDays(req.getDays());
        route.setPrice(req.getPrice());
        route.setMaxGroupSize(req.getMaxGroupSize());
        route.setDeparture(req.getDeparture());
        route.setDestination(req.getDestination());
        route.setHotel(resolveHotel(req.getHotelId()));
        if (req.getStatus() != null) {
            route.setStatus(TourRoute.Status.valueOf(req.getStatus()));
        }
    }

    private Hotel resolveHotel(Long hotelId) {
        if (hotelId == null) {
            return null;
        }

        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new BusinessException(404, "Hotel not found"));
    }
}
