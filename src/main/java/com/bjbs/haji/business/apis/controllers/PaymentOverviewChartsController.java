/**
Project Name: Umroh Enhancement
Project Client: BJBS
Created date: -
Created by: [79] Seftikara
Updated date: -
Updated  by: [79] Seftikara
Descriptions: Payment overview di dashboard
**/
package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.models.ChartDataModel;
import com.bjbs.haji.business.models.ChartDataOverviewPropertiesModel;
import com.bjbs.haji.business.models.ChartDataSetModel;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/dashboard-chart")
public class PaymentOverviewChartsController {


    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @GetMapping("/data-payment")
    private Object getPaymentChartDatas(@Nullable @RequestParam String startDate, @Nullable @RequestParam String finishDate) {
        ChartDataOverviewPropertiesModel cP=new ChartDataOverviewPropertiesModel();
        ChartDataModel cD=new ChartDataModel();

        LocalDate sDt, fDt;
        fDt=(!finishDate.trim().isEmpty())?LocalDate.from(DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(finishDate)):LocalDate.now();
        sDt=(!startDate.trim().isEmpty())?LocalDate.from(DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(startDate)):fDt.minusDays(30);

        cP.setTitle("Data Payment: "+DateTimeFormatter.ofPattern("dd MMM yyyy").format(sDt)+" - "+DateTimeFormatter.ofPattern("dd MMM yyyy").format(fDt));

        LocalDate datePointer;
        datePointer=LocalDate.ofEpochDay(sDt.toEpochDay());

        List<Object> labels=new ArrayList<>();
        List<ChartDataSetModel> paymentDatasetList = new ArrayList<>();
        ChartDataSetModel setoranAwalDataset = new ChartDataSetModel();
        ChartDataSetModel setoranPelunasanDataset = new ChartDataSetModel();
        List<Long> setoranAwalList = new ArrayList<>();
        List<Long> setoranPelunasanList = new ArrayList<>();

        setoranAwalDataset.setLabel("Setoran Awal");
        setoranAwalDataset.setFill("start");
        setoranAwalDataset.setBackgroundColor("rgb(143, 153, 255)");
        setoranAwalDataset.setBorderColor("rgb(55, 60, 255)");
        setoranAwalDataset.setPointBackgroundColor("#ffffff");
        setoranAwalDataset.setPointHoverBackgroundColor("rgb(0,123,255)");
        setoranAwalDataset.setBorderWidth(1.5D);
        setoranAwalDataset.setPointRadius(0D);
        setoranAwalDataset.setPointHoverRadius(3D);

        setoranPelunasanDataset.setLabel("Setoran Pelunasan");
        setoranPelunasanDataset.setFill("start");
        setoranPelunasanDataset.setBackgroundColor("rgb(255, 166, 190)");
        setoranPelunasanDataset.setBorderColor("rgb(255, 58, 71)");
        setoranPelunasanDataset.setPointBackgroundColor("#ffffff");
        setoranPelunasanDataset.setPointHoverBackgroundColor("rgb(255, 58, 71)");
        setoranPelunasanDataset.setBorderWidth(1.5D);
        setoranPelunasanDataset.setPointRadius(0D);
        setoranPelunasanDataset.setPointHoverRadius(3D);

        do {
            labels.add(new String[]{datePointer.getMonth().getDisplayName(TextStyle.SHORT, Locale.US), String.valueOf(datePointer.getDayOfMonth())});
            setoranAwalList.add(setoranAwalRepository.getCountByDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(datePointer)));
            setoranPelunasanList.add(setoranPelunasanRepository.getCountByDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(datePointer)));
            datePointer=datePointer.plusDays(1);
        } while (!datePointer.equals(fDt.plusDays(1)));

        setoranAwalDataset.setData(setoranAwalList);
        setoranPelunasanDataset.setData(setoranPelunasanList);
        paymentDatasetList.add(setoranAwalDataset);
        paymentDatasetList.add(setoranPelunasanDataset);
        cD.setLabels(labels);
        cD.setDatasets(paymentDatasetList);
        cP.setChartData(cD);
        return ResponseEntity.ok().body(cP);
    }
}