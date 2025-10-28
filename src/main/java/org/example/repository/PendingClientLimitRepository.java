package org.example.repository;

import org.example.entity.ClientLimit;
import org.example.entity.PendingClientLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingClientLimitRepository extends JpaRepository<PendingClientLimit, UUID> {

    @Query(value = "SELECT * FROM pending_clients_limits WHERE client_id = :clientId",  nativeQuery = true)
    List<PendingClientLimit> findAllByClientId(@Param("clientId") long clientId);
}