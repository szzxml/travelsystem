package com.ts.repository;

import com.ts.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderNo(String orderNo);
    boolean existsByOrderNo(String orderNo);

    @Override
    @EntityGraph(attributePaths = {"user", "route"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user", "route"})
    Optional<Order> findById(Long id);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :start")
    long countByCreatedAtAfter(LocalDateTime start);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'PAID' OR o.status = 'COMPLETED'")
    BigDecimal sumCompletedAmount();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPending();
}
