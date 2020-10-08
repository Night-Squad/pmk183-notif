package com.bjbs.haji.business.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataSetModel {

    private String label;
    private String fill;
    private List<Long> data;
    private String backgroundColor;
    private String borderColor;
    private String pointBackgroundColor;
    private String pointHoverBackgroundColor;
    private Double borderWidth;
    private Double pointRadius;
    private Double pointHoverRadius;

}