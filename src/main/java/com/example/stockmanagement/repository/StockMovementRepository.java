package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.StockMovement;
import com.example.stockmanagement.model.MovementType;
import com.example.stockmanagement.model.MovementSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    // Keep this method for DashboardService
    List<StockMovement> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Method for filtering with dates and type
    Page<StockMovement> findByDateGreaterThanEqualAndDateLessThanEqualAndType(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            MovementType type,
            Pageable pageable);

    // Method for filtering with only dates
    Page<StockMovement> findByDateGreaterThanEqualAndDateLessThanEqual(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            Pageable pageable);

    // Method for filtering with dates and source
    Page<StockMovement> findByDateGreaterThanEqualAndDateLessThanEqualAndSource(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            MovementSource source,
            Pageable pageable);

    // Method for filtering with dates, type and source
    Page<StockMovement> findByDateGreaterThanEqualAndDateLessThanEqualAndTypeAndSource(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            MovementType type, 
            MovementSource source, 
            Pageable pageable);

    // Method for filtering without dates
    Page<StockMovement> findByTypeAndSource(
            MovementType type, 
            MovementSource source, 
            Pageable pageable);

    // Method for filtering with only type
    Page<StockMovement> findByType(MovementType type, Pageable pageable);

    // Method for filtering with only source
    Page<StockMovement> findBySource(MovementSource source, Pageable pageable);
}