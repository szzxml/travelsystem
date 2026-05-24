package com.ts.service.impl;

import com.ts.common.PageResult;
import com.ts.dto.OrderRequest;
import com.ts.entity.Order;
import com.ts.entity.TourRoute;
import com.ts.entity.User;
import com.ts.exception.BusinessException;
import com.ts.repository.OrderRepository;
import com.ts.repository.TourRouteRepository;
import com.ts.repository.UserRepository;
import com.ts.service.OrderService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TourRouteRepository routeRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            TourRouteRepository routeRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Order> list(String keyword, String status, int page, int size) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("orderNo"), "%" + keyword + "%"));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), Order.Status.valueOf(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> result = orderRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Order> listByUser(String username, int page, int size) {
        Specification<Order> spec = (root, query, cb) ->
                cb.equal(root.get("user").get("username"), username);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> result = orderRepository.findAll(spec, pageable);
        return new PageResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Order get(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new BusinessException(404, "订单不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order create(String username, OrderRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(401, "用户不存在"));
        TourRoute route = routeRepository.findById(req.getRouteId())
                .orElseThrow(() -> new BusinessException(404, "线路不存在"));
        if (route.getStatus() != TourRoute.Status.PUBLISHED) {
            throw new BusinessException(400, "该线路当前不可预订");
        }
        validatePersons(req.getPersons(), route.getMaxGroupSize());

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setRoute(route);
        order.setPersons(req.getPersons());
        order.setTotalAmount(route.getPrice().multiply(BigDecimal.valueOf(req.getPersons())));
        order.setTravelDate(req.getTravelDate());
        order.setContactName(req.getContactName());
        order.setContactPhone(req.getContactPhone());
        order.setRemark(req.getRemark());
        return orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order updateStatus(Long id, String status, String rejectReason) {
        Order order = get(id);
        Order.Status target = parseStatus(status);
        validateStatusTransition(order.getStatus(), target);
        if (target == Order.Status.REJECTED) {
            if (rejectReason == null || rejectReason.isBlank()) {
                throw new BusinessException(400, "拒绝订单时必须填写原因");
            }
            order.setRejectReason(rejectReason.trim());
        } else {
            order.setRejectReason(null);
        }
        order.setStatus(target);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    private void validatePersons(Integer persons, Integer maxGroupSize) {
        if (persons == null || persons < 1) {
            throw new BusinessException(400, "出行人数不合法");
        }
        if (maxGroupSize != null && maxGroupSize > 0 && persons > maxGroupSize) {
            throw new BusinessException(400, "出行人数超过线路人数上限");
        }
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "TS" + timestamp + random;
    }

    private Order.Status parseStatus(String status) {
        try {
            return Order.Status.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(400, "订单状态不合法");
        }
    }

    private void validateStatusTransition(Order.Status current, Order.Status target) {
        if (current == target) {
            return;
        }
        boolean allowed = switch (current) {
            case PENDING -> target == Order.Status.CONFIRMED
                    || target == Order.Status.REJECTED
                    || target == Order.Status.CANCELLED;
            case CONFIRMED -> target == Order.Status.PAID
                    || target == Order.Status.CANCELLED;
            case PAID -> target == Order.Status.COMPLETED
                    || target == Order.Status.REFUNDING;
            case REFUNDING -> target == Order.Status.REFUNDED;
            case REJECTED, CANCELLED, REFUNDED, COMPLETED -> false;
        };
        if (!allowed) {
            throw new BusinessException(400, "订单状态流转不合法");
        }
    }
}
