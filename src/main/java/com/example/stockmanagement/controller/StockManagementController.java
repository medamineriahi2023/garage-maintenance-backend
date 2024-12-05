package com.example.stockmanagement.controller;

import com.example.stockmanagement.model.*;
import com.example.stockmanagement.service.StockManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/page")
@RequiredArgsConstructor
public class StockManagementController {
    private final StockManagementService stockManagementService;

    @GetMapping("/services")
    public Page<com.example.stockmanagement.model.Service> getServices(Pageable pageable) {
        return stockManagementService.getServices(pageable);
    }

    @PostMapping("/services")
    public com.example.stockmanagement.model.Service addService(@RequestBody Service service) {
        return stockManagementService.addService(service);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        stockManagementService.deleteService(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stock-items")
    public Page<StockItem> getStockItems(Pageable pageable) {
        return stockManagementService.getStockItems(pageable);
    }

    @PostMapping("/stock-items")
    public StockItem addStockItem(@RequestBody StockItem item) {
        return stockManagementService.addStockItem(item);
    }

    @GetMapping("/maintenances")
    public Page<Maintenance> getMaintenances(Pageable pageable) {
        return stockManagementService.getMaintenances(pageable);
    }

    @PostMapping("/maintenances")
    public Maintenance addMaintenance(@RequestBody Maintenance maintenance) {
        return stockManagementService.addMaintenance(maintenance);
    }

    @GetMapping("/stock-movements")
    public Page<StockMovement> getStockMovements(Pageable pageable) {
        return stockManagementService.getStockMovements(pageable);
    }

    @PostMapping("/stock-movements")
    public StockMovement addStockMovement(@RequestBody StockMovement movement) {
        return stockManagementService.addStockMovement(movement);
    }
}