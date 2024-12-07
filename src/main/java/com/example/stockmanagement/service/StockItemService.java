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
public class StockItemService {
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final NotificationService notificationService;

    public Page<StockItem> getStockItems(Pageable pageable) {
        return stockItemRepository.findAll(pageable);
    }

    public Page<StockItem> searchStockItemsByName(String name, Pageable pageable) {
        return stockItemRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    @Transactional
    public StockItem addStockItem(StockItem item) {
        StockItem savedItem = stockItemRepository.save(item);
        
        StockMovement movement = new StockMovement();
        movement.setStockItemId(savedItem.getId());
        movement.setStockItemName(savedItem.getName());
        movement.setQuantity(savedItem.getCurrentQuantity());
        movement.setType(MovementType.IN);
        movement.setSource(MovementSource.ADJUSTMENT);
        movement.setReference("INIT-" + savedItem.getId());
        movement.setDate(LocalDateTime.now());
        movement.setUnitPrice(savedItem.getUnitPrice());
        movement.setTotalPrice(savedItem.getUnitPrice() * savedItem.getCurrentQuantity());
        movement.setNotes("Initial stock for " + savedItem.getName());
        
        stockMovementRepository.save(movement);
        
        checkStockLevel(savedItem);
        return savedItem;
    }
    public Page<StockItem> searchEquipment(String search, Pageable pageable) {
        return stockItemRepository.findByNameContainingIgnoreCase(search, pageable);
    }
    private void checkStockLevel(StockItem item) {
        if (item.getCurrentQuantity() <= item.getMinQuantity()) {
            notificationService.addNotification(item);
        }
    }

    public StockItem getStockItem(Long id){
        return stockItemRepository.findById(id).get();
    }
}