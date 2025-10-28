package org.example.repository;

import org.example.entity.PendingOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PendingOperationRepository extends JpaRepository<PendingOperation, UUID> {

    @Query(value = "SELECT * FROM pending_operations WHERE id = :id",  nativeQuery = true)
    List<PendingOperation> findAllByClientId(@Param("id") long clientId);
}