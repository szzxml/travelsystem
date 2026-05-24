package com.ts.service.impl;

import com.ts.common.PageResult;
import com.ts.dto.AttractionRequest;
import com.ts.entity.Attraction;
import com.ts.exception.BusinessException;
import com.ts.repository.AttractionRepository;
import com.ts.service.AttractionService;
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
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository attractionRepository;

    public AttractionServiceImpl(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Attraction> list(String keyword, int page, int size) {
        Specification<Attraction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Attraction> result = attractionRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Attraction get(Long id) {
        return attractionRepository.findById(id).orElseThrow(() -> new BusinessException(404, "景点不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attraction create(AttractionRequest req) {
        Attraction attraction = new Attraction();
        applyRequest(attraction, req);
        return attractionRepository.save(attraction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attraction update(Long id, AttractionRequest req) {
        Attraction attraction = get(id);
        applyRequest(attraction, req);
        return attractionRepository.save(attraction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        attractionRepository.deleteById(id);
    }

    private void applyRequest(Attraction attraction, AttractionRequest req) {
        attraction.setName(req.getName());
        attraction.setLocation(req.getLocation());
        attraction.setDescription(req.getDescription());
        attraction.setCoverImage(req.getCoverImage());
        attraction.setTicketPrice(req.getTicketPrice());
        attraction.setOpenTime(req.getOpenTime());
        attraction.setCapacity(req.getCapacity());
        if (req.getStatus() != null) {
            attraction.setStatus(Attraction.Status.valueOf(req.getStatus()));
        }
    }
}
