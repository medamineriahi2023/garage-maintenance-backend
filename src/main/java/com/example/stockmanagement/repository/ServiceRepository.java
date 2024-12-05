package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}