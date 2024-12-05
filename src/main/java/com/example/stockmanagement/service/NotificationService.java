package com.example.stockmanagement.service;

import com.example.stockmanagement.model.StockItem;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {
    public void addNotification(StockItem item) {
        log.warn("Low stock alert: {} (Current: {}, Minimum: {})", 
            item.getName(), item.getCurrentQuantity(), item.getMinQuantity());
    }
}