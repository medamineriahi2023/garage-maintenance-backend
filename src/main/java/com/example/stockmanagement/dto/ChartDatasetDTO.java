package com.example.stockmanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChartDatasetDTO {
    private String label;
    private List<Double> data;
    private String borderColor;
    private List<String> backgroundColor;
    private Double tension;
}