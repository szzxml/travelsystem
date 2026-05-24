package com.ts.service.impl;

import com.ts.common.PageResult;
import com.ts.dto.HotelRequest;
import com.ts.entity.Hotel;
import com.ts.exception.BusinessException;
import com.ts.repository.HotelRepository;
import com.ts.service.HotelService;
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
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Hotel> list(String keyword, String status, int page, int size) {
        Specification<Hotel> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.or(
                        cb.like(root.get("name"), "%" + keyword + "%"),
                        cb.like(root.get("city"), "%" + keyword + "%")
                ));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), parseStatus(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Hotel> result = hotelRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Hotel get(Long id) {
        return hotelRepository.findById(id).orElseThrow(() -> new BusinessException(404, "酒店不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Hotel create(HotelRequest req) {
        Hotel hotel = new Hotel();
        applyRequest(hotel, req);
        return hotelRepository.save(hotel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Hotel update(Long id, HotelRequest req) {
        Hotel hotel = get(id);
        applyRequest(hotel, req);
        return hotelRepository.save(hotel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        hotelRepository.deleteById(id);
    }

    private void applyRequest(Hotel hotel, HotelRequest req) {
        hotel.setName(req.getName());
        hotel.setCity(req.getCity());
        hotel.setAddress(req.getAddress());
        hotel.setPhone(req.getPhone());
        hotel.setStarLevel(req.getStarLevel());
        hotel.setDescription(req.getDescription());
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            hotel.setStatus(parseStatus(req.getStatus()));
        }
    }

    private Hotel.Status parseStatus(String status) {
        try {
            return Hotel.Status.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(400, "酒店状态不合法");
        }
    }
}
