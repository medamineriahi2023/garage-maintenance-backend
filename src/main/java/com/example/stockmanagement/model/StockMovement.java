package com.example.stockmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stockItemId;
    private String stockItemName;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private MovementType type;
    @Enumerated(EnumType.STRING)
    private MovementSource source;
    private String reference;
    private LocalDateTime date;
    private double unitPrice;
    private double totalPrice;
    private String notes;
}

