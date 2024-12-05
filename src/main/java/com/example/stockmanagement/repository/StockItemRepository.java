package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
}