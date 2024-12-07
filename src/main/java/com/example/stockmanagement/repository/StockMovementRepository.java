package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}