package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.controllers.utility.CurrencyCode;
import com.bjbs.haji.business.apis.controllers.utility.CurrencyUtil;
import com.bjbs.haji.business.apis.controllers.utility.DateTimeFormatterUtil;
import com.bjbs.haji.business.apis.controllers.utility.StreamUtil;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.SetoranPelunasan;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import com.bjbs.haji.business.repositories.haji.StatusTransaksiRepository;
import com.io.iona.springboot.controllers.HibernateReportController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.io.iona.springboot.sources.HibernateDataUtility;
import com.io.iona.springboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cetak-laporan-transaksi-payment-haji")
public class CetakLaporanTransaksiPaymentHaji extends HibernateReportController<Object, Object> {

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Autowired
    StatusTransaksiRepository statusTransaksiRepository;

    @Autowired
    public CetakLaporanTransaksiPaymentHaji(StorageService storageService) {
        super(storageService, "LaporanTransaksiPaymentHaji");
    }

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<Object, Object> dataSource) throws Exception {

        Date tglAwal = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAwal"));
        Date tglAkhir = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAkhir"));
        long statusId = Long.parseLong(dataSource.getRequestParameterValue("statusId"));
        String branchCode = dataSource.getRequestParameterValue("branchCode");
        String userCode = dataSource.getRequestParameterValue("userCode");
        long channelId = Long.parseLong(dataSource.getRequestParameterValue("channelId"));
        long productId = Long.parseLong(dataSource.getRequestParameterValue("productId"));

        Map<String, Object> header = new HashMap<>();
        Map<String, Object> outerResult = new HashMap<>();

        try {
            header.put("dari", new SimpleDateFormat("dd-MM-yyyy").format(tglAwal));
            header.put("sampai", new SimpleDateFormat("dd-MM-yyyy").format(tglAkhir));
            StatusTransaksi statusTransaksi = new StatusTransaksi();
            if (statusId != 0) {
                statusTransaksi = statusTransaksiRepository.findById(statusId).orElse(null);
            }
            header.put("kriteria_status", statusId != 0 ? statusTransaksi.getNamaStatusTransaksi() : "ALL");
            header.put("instansi", "SISKOHAT");
            if (productId == 1) {
                header.put("produk", "[BPIH] SETORAN AWAL");

            } else {
                header.put("produk", "[BPIH] SETORAN PELUNASAN");
            }
            outerResult.put("header", header);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            if (productId == 1) {
                List<SetoranAwal> list = setoranAwalRepository.getListSetoranAwalPeriod(tglAwal, tglAkhir);
                System.out.println(list);
                List<SetoranAwal> listStatus = list.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getNamaStatusTransaksi()))
                        .collect( Collectors.toList());

                if (statusId != 0) {
                    list = list.stream()
                            .filter(s -> s.getStatusTransaksi().getStatusTransaksiId() == statusId)
                            .collect(Collectors.toList());
                }

                List<SetoranAwal> listBranch = list.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getBranchCode()))
                        .collect( Collectors.toList());;

                if (branchCode != null && !branchCode.equals("")) {
                    list = list.stream()
                            .filter(s -> s.getBranchCode().equals(branchCode))
                            .collect(Collectors.toList());
                }

                if (channelId != 0) {
                    list = list.stream()
                            .filter(s -> s.getChannel().getChannelId() == channelId)
                            .collect(Collectors.toList());
                }

                if (userCode != null && !userCode.equals("")) {
                    list = list.stream()
                            .filter(s -> s.getCreatedBy().equals(userCode))
                            .collect(Collectors.toList());
                }

                System.out.println(listStatus.size());
                System.out.println(listBranch.size());
                System.out.println(list.size());

                List<Map<String, Object>> isiList = new ArrayList<>();
                Double tagihan=0D, fee=0D, total=0D;
                for (SetoranAwal status : listStatus){
                    Map<String, Object> isi = new HashMap<>();
                    List<Map<String, Object>> data1List = new ArrayList<>();
                    isi.put("status", status.getStatusTransaksi().getNamaStatusTransaksi());
                    Double tagihanStat=0D, feeStat=0D, totalStat=0D;
                    for (SetoranAwal setoranAwal : list) {
                        if (setoranAwal.getStatusTransaksi().getStatusTransaksiId() == status.getStatusTransaksi().getStatusTransaksiId()) {
                            Map<String, Object> data1 = new HashMap<>();
                            data1.put("no", data1List.size() + 1);
                            data1.put("tgl_tx", new SimpleDateFormat("dd/MM/yyyy").format(setoranAwal.getTanggalTransaksi()));
                            data1.put("branch_code", setoranAwal.getBranchCode());
                            data1.put("no_rek", setoranAwal.getNoRekening());
                            data1.put("nama", setoranAwal.getNamaJemaah());
                            data1.put("no_arsip", setoranAwal.getTransactionId());
                            data1.put("tagihan", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                            tagihanStat += setoranAwal.getNominalSetoran().doubleValue();
                            data1.put("fee", kursIndonesia.format(0D));
                            feeStat += 0;
                            data1.put("total", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                            totalStat += setoranAwal.getNominalSetoran().doubleValue();
                            data1.put("no_validasi", (setoranAwal.getNoValidasi() != null) ? setoranAwal.getNoValidasi() : "-");
                            data1.put("user", setoranAwal.getCreatedBy());
                            data1.put("channel", setoranAwal.getChannel().getTipeMerchant());
                            data1List.add(data1);
                        }
                    }

                    if (data1List.size() > 0) {
                        isi.put("data1", data1List);
                        isi.put("tagihan_stat", kursIndonesia.format(tagihanStat));
                        tagihan+=tagihanStat;
                        isi.put("fee_stat", kursIndonesia.format(feeStat));
                        fee+=feeStat;
                        isi.put("total_stat", kursIndonesia.format(totalStat));
                        total+=totalStat;
                        isiList.add(isi);
                    }
                }

                outerResult.put("tot_tagihan", kursIndonesia.format(tagihan));
                outerResult.put("tot_fee", kursIndonesia.format(fee));
                outerResult.put("tot_total", kursIndonesia.format(total));
                outerResult.put("isi", isiList);
                return outerResult;
            } else {
                List<SetoranPelunasan> list = setoranPelunasanRepository.getListSetoranPelunasanPeriod(tglAwal, tglAkhir);

                List<SetoranPelunasan> listStatus = list.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getNamaStatusTransaksi()))
                        .collect( Collectors.toList());

                if (statusId != 0) {
                    list = list.stream()
                            .filter(s -> s.getStatusTransaksi().getStatusTransaksiId() == statusId)
                            .collect(Collectors.toList());
                }

                List<SetoranPelunasan> listBranch = list.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getBranchCode()))
                        .collect( Collectors.toList());

                if (branchCode != null && !branchCode.equals("")) {
                    list = list.stream()
                            .filter(s -> s.getBranchCode().equals(branchCode))
                            .collect(Collectors.toList());
                }

                if (channelId != 0) {
                    list = list.stream()
                            .filter(s -> s.getChannel().getChannelId() == channelId)
                            .collect(Collectors.toList());
                }

                if (userCode != null && !userCode.equals("")) {
                    list = list.stream()
                            .filter(s -> s.getCreatedBy().equals(userCode))
                            .collect(Collectors.toList());
                }

                List<Map<String, Object>> isiList = new ArrayList<>();
                Double tagihan=0D, fee=0D, total=0D;
                for (SetoranPelunasan status : listStatus){
                    Map<String, Object> isi = new HashMap<>();
                    List<Map<String, Object>> data1List = new ArrayList<>();
                    isi.put("status", status.getStatusTransaksi().getNamaStatusTransaksi());

                    Double tagihanStat=0D, feeStat=0D, totalStat=0D;
                    for (SetoranPelunasan setoranPelunasan : list) {
                        if (setoranPelunasan.getStatusTransaksi().getStatusTransaksiId() == status.getStatusTransaksi().getStatusTransaksiId()) {
                            Map<String, Object> data1 = new HashMap<>();
                            data1.put("no", data1List.size()+1);
                            data1.put("tgl_tx", new SimpleDateFormat("dd/MM/yyyy").format(setoranPelunasan.getTanggalTransaksi()));
                            data1.put("branch_code", setoranPelunasan.getBranchCode());
                            data1.put("no_rek", setoranPelunasan.getNoRekening());
                            data1.put("nama", setoranPelunasan.getNamaJemaah());
                            data1.put("no_arsip", setoranPelunasan.getTransactionId());
                            data1.put("tagihan", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                            tagihanStat+=setoranPelunasan.getNominalSetoran().doubleValue();
                            data1.put("fee", kursIndonesia.format(0D));
                            feeStat+=0;
                            data1.put("total", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                            totalStat+=setoranPelunasan.getNominalSetoran().doubleValue();
                            data1.put("no_validasi", (setoranPelunasan.getNoPorsi()!=null)?setoranPelunasan.getNoPorsi():"-");
                            data1.put("user", setoranPelunasan.getCreatedBy());
                            data1.put("channel", setoranPelunasan.getChannel().getTipeMerchant());
                            data1List.add(data1);
                        }
                    }
                    if (data1List.size() > 0) {
                        isi.put("data1", data1List);
                        isi.put("tagihan_stat", kursIndonesia.format(tagihanStat));
                        tagihan+=tagihanStat;
                        isi.put("fee_stat", kursIndonesia.format(feeStat));
                        fee+=feeStat;
                        isi.put("total_stat", kursIndonesia.format(totalStat));
                        total+=totalStat;
                        isiList.add(isi);
                    }
                }

                outerResult.put("tot_tagihan", kursIndonesia.format(tagihan));
                outerResult.put("tot_fee", kursIndonesia.format(fee));
                outerResult.put("tot_total", kursIndonesia.format(total));
                outerResult.put("isi", isiList);
                return outerResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
    
}