package com.example.stockmanagement;

import com.example.stockmanagement.model.Service;
import com.example.stockmanagement.model.StockItem;
import com.example.stockmanagement.repository.ServiceRepository;
import com.example.stockmanagement.service.StockManagementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StockManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockManagementApplication.class, args);
    }

}