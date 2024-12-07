package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.StockItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {

        Page<StockItem> findByNameContainingIgnoreCase(String name, Pageable pageable);

}