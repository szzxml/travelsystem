package com.ts.repository;

import com.ts.entity.TourRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TourRouteRepository extends JpaRepository<TourRoute, Long>, JpaSpecificationExecutor<TourRoute> {
}
