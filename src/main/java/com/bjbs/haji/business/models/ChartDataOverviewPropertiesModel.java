package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataOverviewPropertiesModel {

    private String title;
    private ChartDataModel chartData;
    
}