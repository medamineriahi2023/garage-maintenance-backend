package com.example.stockmanagement.controller;

import com.example.stockmanagement.model.StockMovement;
import com.example.stockmanagement.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {
    private final StockMovementService stockMovementService;

    @GetMapping
    public Page<StockMovement> getStockMovements(Pageable pageable) {
        return stockMovementService.getStockMovements(pageable);
    }

    @PostMapping
    public StockMovement addStockMovement(@RequestBody StockMovement movement) {
        return stockMovementService.addStockMovement(movement);
    }
}