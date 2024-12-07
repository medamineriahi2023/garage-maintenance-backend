package com.example.stockmanagement.service;

import com.example.stockmanagement.dto.*;
import com.example.stockmanagement.model.*;
import com.example.stockmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final MaintenanceRepository maintenanceRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockItemRepository stockItemRepository;

    public DashboardDataDTO getDashboardData(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = getStartDate(now, period);
        
        List<Maintenance> currentMaintenances = maintenanceRepository.findByDateBetween(startDate, now);
        List<Maintenance> previousMaintenances = maintenanceRepository.findByDateBetween(
            startDate.minus(1, getPeriodUnit(period)),
            startDate
        );
        
        List<StockMovement> currentMovements = stockMovementRepository.findByDateBetween(startDate, now);
        List<StockMovement> previousMovements = stockMovementRepository.findByDateBetween(
            startDate.minus(1, getPeriodUnit(period)),
            startDate
        );

        DashboardDataDTO dashboard = new DashboardDataDTO();
        dashboard.setSummary(calculateSummary(currentMaintenances, previousMaintenances, currentMovements, previousMovements));
        dashboard.setRevenueExpensesChart(createRevenueExpensesChart(currentMaintenances, currentMovements, period));
        dashboard.setProfitChart(createProfitChart(currentMaintenances, currentMovements, period));
        dashboard.setServicesChart(createServicesChart(currentMaintenances));
        dashboard.setStockChart(createStockChart());

        return dashboard;
    }

    private DashboardSummaryDTO calculateSummary(
            List<Maintenance> currentMaintenances,
            List<Maintenance> previousMaintenances,
            List<StockMovement> currentMovements,
            List<StockMovement> previousMovements
    ) {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        double currentRevenue = currentMaintenances.stream().mapToDouble(Maintenance::getFinalPrice).sum();
        double previousRevenue = previousMaintenances.stream().mapToDouble(Maintenance::getFinalPrice).sum();

        double currentExpenses = calculateTotalExpenses(currentMaintenances, currentMovements);
        double previousExpenses = calculateTotalExpenses(previousMaintenances, previousMovements);

        summary.setRevenue(currentRevenue);
        summary.setRevenueChange(calculateChange(currentRevenue, previousRevenue));
        summary.setExpenses(currentExpenses);
        summary.setExpensesChange(calculateChange(currentExpenses, previousExpenses));
        summary.setProfit(currentRevenue - currentExpenses);
        summary.setProfitChange(calculateChange(
            currentRevenue - currentExpenses,
            previousRevenue - previousExpenses
        ));
        summary.setMaintenanceCount(currentMaintenances.size());
        summary.setMaintenanceCountChange(calculateChange(
            currentMaintenances.size(),
            previousMaintenances.size()
        ));

        return summary;
    }

    private ChartDataDTO createRevenueExpensesChart(
            List<Maintenance> maintenances,
            List<StockMovement> movements,
            String period
    ) {
        List<String> labels = generateTimeLabels(period);
        List<Double> revenueData = new ArrayList<>(Collections.nCopies(labels.size(), 0.0));
        List<Double> expensesData = new ArrayList<>(Collections.nCopies(labels.size(), 0.0));

        maintenances.forEach(maintenance -> {
            int index = getTimeIndex(maintenance.getDate(), period);
            if (index >= 0) {
                revenueData.set(index, revenueData.get(index) + maintenance.getFinalPrice());
                double maintenanceExpenses = maintenance.getEquipmentUsed().stream()
                    .mapToDouble(StockItem::getUnitPrice)
                    .sum();
                expensesData.set(index, expensesData.get(index) + maintenanceExpenses);
            }
        });

        movements.stream()
            .filter(m -> m.getType() == MovementType.IN && m.getSource() == MovementSource.PURCHASE)
            .forEach(movement -> {
                int index = getTimeIndex(movement.getDate(), period);
                if (index >= 0) {
                    expensesData.set(index, expensesData.get(index) + movement.getTotalPrice());
                }
            });

        ChartDataDTO chart = new ChartDataDTO();
        chart.setLabels(labels);
        
        List<ChartDatasetDTO> datasets = new ArrayList<>();
        
        ChartDatasetDTO revenueDataset = new ChartDatasetDTO();
        revenueDataset.setLabel("Revenus");
        revenueDataset.setData(revenueData);
        revenueDataset.setBorderColor("#3B82F6");
        revenueDataset.setTension(0.4);
        datasets.add(revenueDataset);

        ChartDatasetDTO expensesDataset = new ChartDatasetDTO();
        expensesDataset.setLabel("Dépenses");
        expensesDataset.setData(expensesData);
        expensesDataset.setBorderColor("#EC4899");
        expensesDataset.setTension(0.4);
        datasets.add(expensesDataset);

        chart.setDatasets(datasets);
        return chart;
    }

    private ChartDataDTO createProfitChart(
            List<Maintenance> maintenances,
            List<StockMovement> movements,
            String period
    ) {
        List<String> labels = generateTimeLabels(period);
        List<Double> profitData = new ArrayList<>(Collections.nCopies(labels.size(), 0.0));

        maintenances.forEach(maintenance -> {
            int index = getTimeIndex(maintenance.getDate(), period);
            if (index >= 0) {
                double profit = maintenance.getFinalPrice() - maintenance.getEquipmentUsed().stream()
                    .mapToDouble(StockItem::getUnitPrice)
                    .sum();
                profitData.set(index, profitData.get(index) + profit);
            }
        });

        movements.stream()
            .filter(m -> m.getType() == MovementType.IN && m.getSource() == MovementSource.PURCHASE)
            .forEach(movement -> {
                int index = getTimeIndex(movement.getDate(), period);
                if (index >= 0) {
                    profitData.set(index, profitData.get(index) - movement.getTotalPrice());
                }
            });

        ChartDataDTO chart = new ChartDataDTO();
        chart.setLabels(labels);

        ChartDatasetDTO dataset = new ChartDatasetDTO();
        dataset.setLabel("Bénéfice");
        dataset.setData(profitData);
        dataset.setBackgroundColor(Collections.singletonList("#22C55E"));

        chart.setDatasets(Collections.singletonList(dataset));
        return chart;
    }

    private ChartDataDTO createServicesChart(List<Maintenance> maintenances) {
        Map<String, Long> serviceStats = maintenances.stream()
            .collect(Collectors.groupingBy(Maintenance::getServiceName, Collectors.counting()));

        ChartDataDTO chart = new ChartDataDTO();
        chart.setLabels(new ArrayList<>(serviceStats.keySet()));

        ChartDatasetDTO dataset = new ChartDatasetDTO();
        dataset.setData(serviceStats.values().stream()
            .map(Long::doubleValue)
            .collect(Collectors.toList()));
        dataset.setBackgroundColor(Arrays.asList("#3B82F6", "#EC4899", "#22C55E", "#F59E0B"));

        chart.setDatasets(Collections.singletonList(dataset));
        return chart;
    }

    private ChartDataDTO createStockChart() {
        List<StockItem> stockItems = stockItemRepository.findAll();

        ChartDataDTO chart = new ChartDataDTO();
        chart.setLabels(stockItems.stream()
            .map(StockItem::getName)
            .collect(Collectors.toList()));

        ChartDatasetDTO dataset = new ChartDatasetDTO();
        dataset.setData(stockItems.stream()
            .map(item -> item.getCurrentQuantity() * item.getRealPrice())
            .collect(Collectors.toList()));
        dataset.setBackgroundColor(Arrays.asList("#3B82F6", "#EC4899", "#22C55E", "#F59E0B"));

        chart.setDatasets(Collections.singletonList(dataset));
        return chart;
    }

    private double calculateTotalExpenses(List<Maintenance> maintenances, List<StockMovement> movements) {
        double maintenanceExpenses = maintenances.stream()
            .flatMap(m -> m.getEquipmentUsed().stream())
            .mapToDouble(StockItem::getUnitPrice)
            .sum();

        double stockPurchaseExpenses = movements.stream()
            .filter(m -> m.getType() == MovementType.IN && m.getSource() == MovementSource.PURCHASE)
            .mapToDouble(StockMovement::getTotalPrice)
            .sum();

        return maintenanceExpenses + stockPurchaseExpenses;
    }

    private double calculateChange(double current, double previous) {
        if (previous == 0) return 100.0;
        return Math.round((current - previous) / previous * 1000.0) / 10.0;
    }

    private LocalDateTime getStartDate(LocalDateTime now, String period) {
        return switch (period) {
            case "day" -> now.truncatedTo(ChronoUnit.DAYS);
            case "week" -> now.minus(7, ChronoUnit.DAYS);
            case "month" -> now.minus(1, ChronoUnit.MONTHS);
            case "year" -> now.minus(1, ChronoUnit.YEARS);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private ChronoUnit getPeriodUnit(String period) {
        return switch (period) {
            case "day" -> ChronoUnit.DAYS;
            case "week" -> ChronoUnit.WEEKS;
            case "month" -> ChronoUnit.MONTHS;
            case "year" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private List<String> generateTimeLabels(String period) {
        return switch (period) {
            case "day" -> IntStream.range(0, 24)
                .mapToObj(i -> i + "h")
                .collect(Collectors.toList());
            case "week" -> IntStream.range(0, 7)
                .mapToObj(i -> LocalDateTime.now().minus(i, ChronoUnit.DAYS)
                    .getDayOfWeek().toString().substring(0, 3))
                .collect(Collectors.toList());
            case "month" -> IntStream.range(0, 30)
                .mapToObj(i -> String.valueOf(i + 1))
                .collect(Collectors.toList());
            case "year" -> IntStream.range(0, 12)
                .mapToObj(i -> LocalDateTime.now().minus(i, ChronoUnit.MONTHS)
                    .getMonth().toString().substring(0, 3))
                .collect(Collectors.toList());
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private int getTimeIndex(LocalDateTime date, String period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period) {
            case "day" -> date.getHour();
            case "week" -> (int) ChronoUnit.DAYS.between(date, now);
            case "month" -> date.getDayOfMonth() - 1;
            case "year" -> date.getMonthValue() - 1;
            default -> -1;
        };
    }
}