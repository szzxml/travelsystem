package com.ts.repository;

import com.ts.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttractionRepository extends JpaRepository<Attraction, Long>, JpaSpecificationExecutor<Attraction> {
}
