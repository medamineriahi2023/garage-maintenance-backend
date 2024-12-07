package com.example.stockmanagement.controller;

import com.example.stockmanagement.model.Maintenance;
import com.example.stockmanagement.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @GetMapping
    public Page<Maintenance> getMaintenances(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return maintenanceService.getMaintenances(search, pageable);
    }
    @PostMapping
    public ResponseEntity<Maintenance> addMaintenance(@RequestBody Maintenance maintenance) {
        try {
            Maintenance savedMaintenance = maintenanceService.addMaintenance(maintenance);
            return ResponseEntity.ok(savedMaintenance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}