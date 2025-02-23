package com.example.stockmanagement.service;

import com.example.stockmanagement.model.StockItem;
import com.example.stockmanagement.model.StockMovement;
import com.example.stockmanagement.model.MovementType;
import com.example.stockmanagement.model.MovementSource;
import com.example.stockmanagement.repository.StockItemRepository;
import com.example.stockmanagement.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final StockItemRepository stockItemRepository;
    private final NotificationService notificationService;

    public Page<StockMovement> getStockMovements(
            LocalDateTime startDate,
            LocalDateTime endDate,
            MovementType type,
            MovementSource source,
            Pageable pageable) {
        
        // If we have both dates
        if (startDate != null && endDate != null) {
            if (type != null && source != null) {
                return stockMovementRepository.findByDateGreaterThanEqualAndDateLessThanEqualAndTypeAndSource(
                    startDate, endDate, type, source, pageable);
            }
            if (type != null) {
                return stockMovementRepository.findByDateGreaterThanEqualAndDateLessThanEqualAndType(
                    startDate, endDate, type, pageable);
            }
            if (source != null) {
                return stockMovementRepository.findByDateGreaterThanEqualAndDateLessThanEqualAndSource(
                    startDate, endDate, source, pageable);
            }
            return stockMovementRepository.findByDateGreaterThanEqualAndDateLessThanEqual(
                startDate, endDate, pageable);
        }
        
        // If we don't have dates
        if (type != null && source != null) {
            return stockMovementRepository.findByTypeAndSource(type, source, pageable);
        }
        if (type != null) {
            return stockMovementRepository.findByType(type, pageable);
        }
        if (source != null) {
            return stockMovementRepository.findBySource(source, pageable);
        }
        
        return stockMovementRepository.findAll(pageable);
    }

    @Transactional
    public StockMovement addStockMovement(StockMovement movement) {
        if (movement.getDate() == null) {
            movement.setDate(LocalDateTime.now());
        }
        StockItem stockItem = stockItemRepository.findById(movement.getStockItemId())
            .orElseThrow(() -> new RuntimeException("Stock item not found"));

        if (movement.getType() == MovementType.OUT && 
            stockItem.getCurrentQuantity() < movement.getQuantity()) {
            throw new RuntimeException("Insufficient stock quantity");
        }

        movement.setUnitPrice(stockItem.getUnitPrice());
        movement.setTotalPrice(movement.getQuantity() * movement.getUnitPrice());
        movement.setStockItemName(stockItem.getName());

        updateStockQuantity(stockItem, movement.getQuantity(), movement.getType());
        return stockMovementRepository.save(movement);
    }

    private void updateStockQuantity(StockItem item, int quantity, MovementType type) {
        if (type == MovementType.IN) {
            item.setCurrentQuantity(item.getCurrentQuantity() + quantity);
        } else {
            item.setCurrentQuantity(Math.max(0, item.getCurrentQuantity() - quantity));
        }
        
        stockItemRepository.save(item);
        checkStockLevel(item);
    }

    private void checkStockLevel(StockItem item) {
        if (item.getCurrentQuantity() <= item.getMinQuantity()) {
            notificationService.addNotification(item);
        }
    }
}