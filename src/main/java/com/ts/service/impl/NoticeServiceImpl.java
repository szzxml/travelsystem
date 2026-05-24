package com.ts.service.impl;

import com.ts.common.PageResult;
import com.ts.dto.NoticeRequest;
import com.ts.entity.Notice;
import com.ts.exception.BusinessException;
import com.ts.repository.NoticeRepository;
import com.ts.service.NoticeService;
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
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Notice> list(String keyword, int page, int size) {
        Specification<Notice> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("title"), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notice> result = noticeRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Notice get(Long id) {
        return noticeRepository.findById(id).orElseThrow(() -> new BusinessException(404, "公告不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice create(NoticeRequest req) {
        Notice notice = new Notice();
        applyRequest(notice, req);
        return noticeRepository.save(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice update(Long id, NoticeRequest req) {
        Notice notice = get(id);
        applyRequest(notice, req);
        return noticeRepository.save(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    private void applyRequest(Notice notice, NoticeRequest req) {
        notice.setTitle(req.getTitle());
        notice.setContent(req.getContent());
        if (req.getType() != null) {
            notice.setType(Notice.Type.valueOf(req.getType()));
        }
        if (req.getPublished() != null) {
            notice.setPublished(req.getPublished());
        }
    }
}
