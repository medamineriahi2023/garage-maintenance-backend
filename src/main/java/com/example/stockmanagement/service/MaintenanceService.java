package com.example.stockmanagement.service;

import com.example.stockmanagement.model.Maintenance;
import com.example.stockmanagement.model.StockMovement;
import com.example.stockmanagement.model.MovementType;
import com.example.stockmanagement.model.MovementSource;
import com.example.stockmanagement.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final StockMovementService stockMovementService;

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
            
            stockMovementService.addStockMovement(movement);
        });
        
        return savedMaintenance;
    }
}