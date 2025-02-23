package com.example.stockmanagement.controller;

import com.example.stockmanagement.model.StockMovement;
import com.example.stockmanagement.model.MovementType;
import com.example.stockmanagement.model.MovementSource;
import com.example.stockmanagement.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {
    private final StockMovementService stockMovementService;

    @GetMapping
    public Page<StockMovement> getStockMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String source,
            Pageable pageable) {
        
        MovementType movementType = type != null ? MovementType.valueOf(type) : null;
        MovementSource movementSource = source != null ? MovementSource.valueOf(source) : null;
        
        return stockMovementService.getStockMovements(startDate, endDate, movementType, movementSource, pageable);
    }

    @PostMapping
    public StockMovement addStockMovement(@RequestBody StockMovement movement) {
        return stockMovementService.addStockMovement(movement);
    }
}