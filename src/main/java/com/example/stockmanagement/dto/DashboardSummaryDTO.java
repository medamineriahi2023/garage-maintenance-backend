package com.example.stockmanagement.dto;

import lombok.Data;

@Data
public class DashboardSummaryDTO {
    private double revenue;
    private double revenueChange;
    private double expenses;
    private double expensesChange;
    private double profit;
    private double profitChange;
    private long maintenanceCount;
    private double maintenanceCountChange;
}