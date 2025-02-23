package com.example.stockmanagement.service;

import com.example.stockmanagement.model.StockItem;
import com.example.stockmanagement.model.StockMovement;
import com.example.stockmanagement.model.MovementType;
import com.example.stockmanagement.model.MovementSource;
import com.example.stockmanagement.model.StockStatus;
import com.example.stockmanagement.repository.StockItemRepository;
import com.example.stockmanagement.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockItemService {
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final NotificationService notificationService;

    public Page<StockItem> getStockItems(String search, StockStatus status, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            Page<StockItem> items = stockItemRepository.findByNameContainingIgnoreCase(search, pageable);
            return filterByStatus(items, status);
        }
        
        if (status != null && status != StockStatus.ALL) {
            return stockItemRepository.findByStatus(status.name(), pageable);
        }
        
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
        
        checkLowStock(savedItem);
        return savedItem;
    }
    public Page<StockItem> searchEquipment(String search, Pageable pageable) {
        return stockItemRepository.findByNameContainingIgnoreCase(search, pageable);
    }
    private void checkLowStock(StockItem item) {
        if (item.getCurrentQuantity() <= item.getMinQuantity()) {
            notificationService.addNotification(item);
        }
    }

    public StockItem getStockItem(Long id){
        return stockItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock item not found"));
    }

    private Page<StockItem> filterByStatus(Page<StockItem> items, StockStatus status) {
        if (status == null || status == StockStatus.ALL) {
            return items;
        }
        
        List<StockItem> filteredList = items.getContent().stream()
            .filter(item -> {
                boolean isLowStock = item.getCurrentQuantity() <= item.getMinQuantity();
                return (status == StockStatus.LOW_STOCK && isLowStock) ||
                       (status == StockStatus.IN_STOCK && !isLowStock);
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(
            filteredList,
            items.getPageable(),
            filteredList.size()
        );
    }
}