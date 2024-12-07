package com.example.stockmanagement.controller;

import com.example.stockmanagement.model.StockItem;
import com.example.stockmanagement.repository.StockItemRepository;
import com.example.stockmanagement.service.StockItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/stock-items")
@RequiredArgsConstructor
public class StockItemController {
    private final StockItemService stockItemService;

    @GetMapping
    public Page<StockItem> getStockItems(Pageable pageable) {
        return stockItemService.getStockItems(pageable);
    }

    @PostMapping
    public StockItem addStockItem(@RequestBody StockItem item) {
        return stockItemService.addStockItem(item);
    }

    @GetMapping("/{id}")
    public StockItem getStockItem(@PathVariable Long id) {
        return stockItemService.getStockItem(id);
    }

    @GetMapping("/equipment")
    public Page<StockItem> searchEquipment(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        System.out.println("im here");
        if (search != null && !search.isEmpty()) {
            return stockItemService.searchEquipment(search, pageable);
        }
        return stockItemService.getStockItems(pageable);
    }
}