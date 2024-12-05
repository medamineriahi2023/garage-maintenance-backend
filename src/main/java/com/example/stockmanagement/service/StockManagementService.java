package com.example.stockmanagement.service;

import com.example.stockmanagement.model.*;
import com.example.stockmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockManagementService {
    private final ServiceRepository serviceRepository;
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final NotificationService notificationService;

    public Page<com.example.stockmanagement.model.Service> getServices(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }

    public com.example.stockmanagement.model.Service addService(com.example.stockmanagement.model.Service service) {
        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    public Page<StockItem> getStockItems(Pageable pageable) {
        return stockItemRepository.findAll(pageable);
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

    public Page<Maintenance> getMaintenances(Pageable pageable) {
        return maintenanceRepository.findAll(pageable);
    }

    @Transactional
    public Maintenance addMaintenance(Maintenance maintenance) {
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        
        maintenance.getEquipmentUsed().forEach(equipment -> {
            StockMovement movement = new StockMovement();
            movement.setStockItemId(equipment.getId());
            movement.setStockItemName(equipment.getName());
            movement.setQuantity(1);
            movement.setType(MovementType.OUT);
            movement.setSource(MovementSource.MAINTENANCE);
            movement.setReference("MAINT-" + savedMaintenance.getId());
            movement.setDate(LocalDateTime.now());
            movement.setUnitPrice(equipment.getUnitPrice());
            movement.setTotalPrice(equipment.getUnitPrice());
            movement.setNotes("Used in maintenance " + savedMaintenance.getId());
            
            addStockMovement(movement);
        });
        
        return savedMaintenance;
    }

    public Page<StockMovement> getStockMovements(Pageable pageable) {
        return stockMovementRepository.findAll(pageable);
    }

    @Transactional
    public StockMovement addStockMovement(StockMovement movement) {
        StockItem stockItem = stockItemRepository.findById(movement.getStockItemId())
            .orElseThrow(() -> new RuntimeException("Stock item not found"));

        if (movement.getType() == MovementType.OUT && 
            stockItem.getCurrentQuantity() < movement.getQuantity()) {
            throw new RuntimeException("Insufficient stock quantity");
        }

        movement.setUnitPrice(stockItem.getUnitPrice());
        movement.setTotalPrice(movement.getQuantity() * movement.getUnitPrice());
        movement.setStockItemName(stockItem.getName());
        movement.setDate(LocalDateTime.now());

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