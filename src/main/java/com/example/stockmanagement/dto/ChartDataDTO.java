package com.example.stockmanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChartDataDTO {
    private List<String> labels;
    private List<ChartDatasetDTO> datasets;
}