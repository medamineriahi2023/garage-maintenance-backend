package com.example.stockmanagement.service;

import com.example.stockmanagement.model.Maintenance;
import com.example.stockmanagement.model.StockMovement;
import com.example.stockmanagement.model.MovementType;
import com.example.stockmanagement.model.MovementSource;
import com.example.stockmanagement.repository.MaintenanceRepository;
import com.example.stockmanagement.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final ServiceRepository serviceRepository;
    private final StockMovementService stockMovementService;

    public Page<Maintenance> getMaintenances(Pageable pageable) {
        return maintenanceRepository.findAll(pageable);
    }

    public Page<Maintenance> getMaintenances(String search, Pageable pageable) {
        if (StringUtils.hasText(search)) {
            return maintenanceRepository.findByClientNameOrClientIdOrRegistrationNumber(search, pageable);
        }
        return maintenanceRepository.findAll(pageable);
    }

    @Transactional
    public Maintenance addMaintenance(Maintenance maintenance) {
        if (maintenance.getDate() == null) {
            maintenance.setDate(LocalDateTime.now());
        }

        // Calculate prices if not set
        if (maintenance.getTotalPrice() == 0) {
            double equipmentTotal = maintenance.getEquipmentUsed().stream()
                .mapToDouble(equipment -> equipment.getUnitPrice())
                .sum();
            
            // Get service price
            com.example.stockmanagement.model.Service service = serviceRepository
                .findById(maintenance.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));
            
            maintenance.setServiceName(service.getServiceName());
            double totalPrice = equipmentTotal + service.getPrice();
            maintenance.setTotalPrice(totalPrice);
            
            // Calculate final price with discount
            double finalPrice = totalPrice * (1 - maintenance.getDiscount() / 100.0);
            maintenance.setFinalPrice(finalPrice);
        }
        
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        
        // Create stock movements for equipment used
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
            movement.setNotes("Used in maintenance " + savedMaintenance.getId() + 
                " for " + maintenance.getCarRegistrationNumber());
            
            stockMovementService.addStockMovement(movement);
        });
        
        return savedMaintenance;
    }
}