package com.example.stockmanagement.controller;

import com.example.stockmanagement.dto.DashboardDataDTO;
import com.example.stockmanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDataDTO> getDashboardData(@RequestParam(defaultValue = "day") String period) {
        try {
            return ResponseEntity.ok(dashboardService.getDashboardData(period));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}