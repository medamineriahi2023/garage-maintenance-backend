package com.example.stockmanagement.controller;

import com.example.stockmanagement.model.Maintenance;
import com.example.stockmanagement.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @GetMapping
    public Page<Maintenance> getMaintenances(Pageable pageable) {
        return maintenanceService.getMaintenances(pageable);
    }

    @PostMapping
    public Maintenance addMaintenance(@RequestBody Maintenance maintenance) {
        return maintenanceService.addMaintenance(maintenance);
    }
}