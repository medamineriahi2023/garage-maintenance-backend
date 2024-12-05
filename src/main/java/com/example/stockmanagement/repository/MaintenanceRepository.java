package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
}