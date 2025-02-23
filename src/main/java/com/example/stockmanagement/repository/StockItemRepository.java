package com.example.stockmanagement.repository;

import com.example.stockmanagement.model.StockItem;
import com.example.stockmanagement.model.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {

        Page<StockItem> findByNameContainingIgnoreCase(String name, Pageable pageable);

        @Query("SELECT s FROM StockItem s WHERE " +
               "(:status = 'LOW_STOCK' AND s.currentQuantity <= s.minQuantity) OR " +
               "(:status = 'IN_STOCK' AND s.currentQuantity > s.minQuantity)")
        Page<StockItem> findByStatus(@Param("status") String status, Pageable pageable);

}