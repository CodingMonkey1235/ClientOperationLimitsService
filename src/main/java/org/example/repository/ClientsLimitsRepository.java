package org.example.repository;

import org.example.entity.ClientLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientsLimitsRepository extends JpaRepository<ClientLimit, Long> {
}