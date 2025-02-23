package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT m FROM Maintenance m WHERE " +
            "LOWER(m.clientName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "m.carRegistrationNumber LIKE CONCAT('%', :search, '%')")
    Page<Maintenance> findByClientNameOrClientIdOrRegistrationNumber(
            @Param("search") String search, Pageable pageable);}