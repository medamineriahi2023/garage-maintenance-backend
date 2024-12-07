package com.example.stockmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long serviceId;
    private String serviceName;
    private Long assignedToUserId;
    private String assignedToUserName;
    private LocalDateTime date;
    private String carRegistrationNumber;
    private String clientName;
    private String description;
    private double totalPrice;
    private double discount;
    private double finalPrice;
    
    @ManyToMany
    @JoinTable(
        name = "maintenance_equipment",
        joinColumns = @JoinColumn(name = "maintenance_id"),
        inverseJoinColumns = @JoinColumn(name = "stock_item_id")
    )
    private List<StockItem> equipmentUsed;
}