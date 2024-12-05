package com.example.stockmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientName;
    
    @ManyToMany
    @JoinTable(
        name = "maintenance_equipment",
        joinColumns = @JoinColumn(name = "maintenance_id"),
        inverseJoinColumns = @JoinColumn(name = "stock_item_id")
    )
    private List<StockItem> equipmentUsed;
}