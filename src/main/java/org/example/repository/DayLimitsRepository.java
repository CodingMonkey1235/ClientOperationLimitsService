package org.example.repository;

import org.example.entity.DayLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayLimitsRepository extends JpaRepository<DayLimit, Long> {
}