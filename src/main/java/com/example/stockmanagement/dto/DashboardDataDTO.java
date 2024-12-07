package com.example.stockmanagement.dto;

import lombok.Data;

@Data
public class DashboardDataDTO {
    private DashboardSummaryDTO summary;
    private ChartDataDTO revenueExpensesChart;
    private ChartDataDTO profitChart;
    private ChartDataDTO servicesChart;
    private ChartDataDTO stockChart;
}