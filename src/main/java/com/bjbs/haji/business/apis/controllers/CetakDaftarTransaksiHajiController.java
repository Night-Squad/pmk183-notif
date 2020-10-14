package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.controllers.utility.CurrencyCode;
import com.bjbs.haji.business.apis.controllers.utility.CurrencyUtil;
import com.bjbs.haji.business.apis.controllers.utility.DateTimeFormatterUtil;
import com.bjbs.haji.business.apis.controllers.utility.StreamUtil;
import com.bjbs.haji.business.models.Channel;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.SetoranPelunasan;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import com.bjbs.haji.business.repositories.haji.StatusTransaksiRepository;
import com.io.iona.springboot.controllers.HibernateReportController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.io.iona.springboot.sources.HibernateDataUtility;
import com.io.iona.springboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cetak-daftar-transaksi-haji")
public class CetakDaftarTransaksiHajiController extends HibernateReportController<Object, Object> {
    
    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Autowired
    public CetakDaftarTransaksiHajiController(StorageService storageService) {
        super(storageService, "DaftarTransaksiHaji");
    }

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<Object, Object> dataSource) throws Exception {

        Date tglAwal = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAwal"));
        Date tglAkhir = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAkhir"));
        String teller = dataSource.getRequestParameterValue("teller");

        Map<String, Object> header = new HashMap<>();
        Map<String, Object> outerResult = new HashMap<>();

        try {
            header.put("dari", new SimpleDateFormat("dd-MM-yyyy").format(tglAwal));
            header.put("sampai", new SimpleDateFormat("dd-MM-yyyy").format(tglAkhir));
            header.put("teller", teller.equals("0") ? "ALL" : teller);
            outerResult.put("header", header);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            List<Map<String, Object>> isiList = new ArrayList<>();
            Map<String, Object> isiAwal = new HashMap<>();
            Map<String, Object> isiPelunasan = new HashMap<>();

            List<SetoranAwal> listSetoranAwal = setoranAwalRepository.getListSetoranAwalPeriod(tglAwal, tglAkhir);
            List<SetoranPelunasan> listSetoranPelunasan = setoranPelunasanRepository.getListSetoranPelunasanPeriod(tglAwal, tglAkhir);

//            listSetoranAwal = listSetoranAwal.stream()
//                    .filter(s -> s.getStatusTransaksi().getStatusTransaksiId() == 3)
//                    .collect(Collectors.toList());
//
//            listSetoranPelunasan = listSetoranPelunasan.stream()
//                    .filter(s -> s.getStatusTransaksi().getStatusTransaksiId() == 3)
//                    .collect(Collectors.toList());

            if (!teller.equals("0")) {
                listSetoranAwal = listSetoranAwal.stream()
                        .filter(s -> s.getCreatedBy().equals(teller))
                        .collect(Collectors.toList());

                isiAwal.put("instansi", "SISKOHAT");
                isiAwal.put("produk", "[BPIH] SETORAN AWAL");
                List<Map<String, Object>> outerDataAwalList = new ArrayList<>();
                Map<String, Object> outerDataAwal = new HashMap<>();
                if (listSetoranAwal.size() > 0) {
                    outerDataAwal.put("branch_code", listSetoranAwal.get(0).getBranchCode());
                    outerDataAwal.put("user", teller);
                    List<Map<String, Object>> dataList=new ArrayList<>();
                    Double nominal=0D, biayaAdmin=0D, total=0D;
                    for (SetoranAwal setoranAwal : listSetoranAwal) {
                        Map<String, Object> data=new HashMap<>();
                        data.put("no", dataList.size()+1);
                        data.put("no_arsip", setoranAwal.getTransactionId());
                        data.put("nim", setoranAwal.getNoRekening());
                        data.put("nama", setoranAwal.getNamaJemaah());
                        data.put("status", setoranAwal.getStatusTransaksi().getNamaStatusTransaksi());
                        data.put("nominal", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                        nominal+=setoranAwal.getNominalSetoran().doubleValue();
                        data.put("biaya_admin", kursIndonesia.format(0D));
                        biayaAdmin+=0;
                        data.put("total", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                        total+=setoranAwal.getNominalSetoran().doubleValue();
                        dataList.add(data);
                    }
                    outerDataAwal.put("data", dataList);
                    outerDataAwalList.add(outerDataAwal);

                    isiAwal.put("outer_data", outerDataAwal);
                    isiAwal.put("tot_nominal", kursIndonesia.format(nominal));
                    isiAwal.put("tot_biaya", kursIndonesia.format(biayaAdmin));
                    isiAwal.put("tot_total", kursIndonesia.format(total));
                    isiList.add(isiAwal);
                }

                listSetoranPelunasan = listSetoranPelunasan.stream()
                        .filter(s -> s.getCreatedBy().equals(teller))
                        .collect(Collectors.toList());

                isiPelunasan.put("instansi", "SISKOHAT");
                isiPelunasan.put("produk", "[BPIH] SETORAN PELUNASAN");
                List<Map<String, Object>> outerDataPelunasanList = new ArrayList<>();
                Map<String, Object> outerDataPelunasan = new HashMap<>();
                if (listSetoranPelunasan.size() > 0) {
                    outerDataPelunasan.put("branch_code", listSetoranPelunasan.get(0).getBranchCode());
                    outerDataPelunasan.put("user", teller);
                    List<Map<String, Object>> dataList=new ArrayList<>();
                    Double nominal=0D, biayaAdmin=0D, total=0D;
                    for (SetoranPelunasan setoranPelunasan : listSetoranPelunasan) {
                        Map<String, Object> data=new HashMap<>();
                        data.put("no", dataList.size()+1);
                        data.put("no_arsip", setoranPelunasan.getTransactionId());
                        data.put("no_rek", setoranPelunasan.getNoRekening());
                        data.put("nama", setoranPelunasan.getNamaJemaah());
                        data.put("status", setoranPelunasan.getStatusTransaksi().getNamaStatusTransaksi());
                        data.put("nominal", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                        nominal+=setoranPelunasan.getNominalSetoran().doubleValue();
                        data.put("biaya_admin", kursIndonesia.format(0D));
                        biayaAdmin+=0;
                        data.put("total", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                        total+=setoranPelunasan.getNominalSetoran().doubleValue();
                        dataList.add(data);
                    }
                    outerDataPelunasan.put("data", dataList);
                    outerDataPelunasanList.add(outerDataPelunasan);

                    isiPelunasan.put("outer_data", outerDataPelunasanList);
                    isiPelunasan.put("tot_nominal", kursIndonesia.format(nominal));
                    isiPelunasan.put("tot_biaya", kursIndonesia.format(biayaAdmin));
                    isiPelunasan.put("tot_total", kursIndonesia.format(total));
                    isiList.add(isiPelunasan);
                }

                outerResult.put("isi", isiList);
                return outerResult;

            } else {
                List<SetoranAwal> listUserAwal = listSetoranAwal.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getCreatedBy()))
                        .collect( Collectors.toList());

                List<SetoranAwal> listBranchAwal = listUserAwal.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getBranchCode()))
                        .collect( Collectors.toList());

                isiAwal.put("instansi", "SISKOHAT");
                isiAwal.put("produk", "[BPIH] SETORAN AWAL");
                List<Map<String, Object>> outerDataAwalList = new ArrayList<>();

                if (listSetoranAwal.size() > 0) {
                    Double nominal=0D, biayaAdmin=0D, total=0D;
                    int index = 1;
                    for (SetoranAwal branchAwal : listBranchAwal) {
                        Map<String, Object> outerDataAwal = new HashMap<>();
                        outerDataAwal.put("branch_code", branchAwal.getBranchCode());
                        for (SetoranAwal userAwal : listUserAwal) {
                            if (branchAwal.getBranchCode().equals(userAwal.getBranchCode())) {
                                outerDataAwal.put("user", userAwal.getCreatedBy());
                            } else {
                                continue;
                            }
                            List<Map<String, Object>> dataList=new ArrayList<>();
                            for (SetoranAwal setoranAwal : listSetoranAwal) {
                                if (setoranAwal.getCreatedBy().equals(userAwal.getCreatedBy())) {
                                    Map<String, Object> data=new HashMap<>();
                                    data.put("no", index);
                                    data.put("no_arsip", setoranAwal.getTransactionId());
                                    data.put("no_rek", setoranAwal.getNoRekening());
                                    data.put("nama", setoranAwal.getNamaJemaah());
                                    data.put("status", setoranAwal.getStatusTransaksi().getNamaStatusTransaksi());
                                    data.put("nominal", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                    nominal+=setoranAwal.getNominalSetoran().doubleValue();
                                    data.put("biaya_admin", kursIndonesia.format(0D));
                                    biayaAdmin+=0;
                                    data.put("total", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                    total+=setoranAwal.getNominalSetoran().doubleValue();
                                    dataList.add(data);
                                    index++;
                                } else {
                                    continue;
                                }
                            }
                            outerDataAwal.put("data", dataList);
                            outerDataAwalList.add(outerDataAwal);
                        }
                    }
                    isiAwal.put("outer_data", outerDataAwalList);
                    isiAwal.put("tot_nominal", kursIndonesia.format(nominal));
                    isiAwal.put("tot_biaya", kursIndonesia.format(biayaAdmin));
                    isiAwal.put("tot_total", kursIndonesia.format(total));
                    isiList.add(isiAwal);
                }

                List<SetoranPelunasan> listUserPelunasan = listSetoranPelunasan.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getCreatedBy()))
                        .collect( Collectors.toList());

                List<SetoranPelunasan> listBranchPelunasan = listUserPelunasan.stream()
                        .filter( StreamUtil.distinctByKey(s -> s.getBranchCode()))
                        .collect( Collectors.toList());

                isiPelunasan.put("instansi", "SISKOHAT");
                isiPelunasan.put("produk", "[BPIH] SETORAN PELUNASAN");
                List<Map<String, Object>> outerDataPelunasanList = new ArrayList<>();

                if (listSetoranPelunasan.size() > 0) {
                    Double nominal=0D, biayaAdmin=0D, total=0D;
                    int index = 1;
                    for (SetoranPelunasan branchPelunasan : listBranchPelunasan) {
                        Map<String, Object> outerDataPelunasan = new HashMap<>();
                        outerDataPelunasan.put("branch_code", branchPelunasan.getBranchCode());
                        for (SetoranPelunasan userPelunasan : listUserPelunasan) {
                            if (branchPelunasan.getBranchCode().equals(userPelunasan.getBranchCode())) {
                                outerDataPelunasan.put("user", userPelunasan.getCreatedBy());
                            } else {
                                continue;
                            }
                            List<Map<String, Object>> dataList=new ArrayList<>();
                            for (SetoranPelunasan setoranPelunasan : listSetoranPelunasan) {
                                if (setoranPelunasan.getCreatedBy().equals(userPelunasan.getCreatedBy())) {
                                    Map<String, Object> data=new HashMap<>();
                                    data.put("no", index);
                                    data.put("no_arsip", setoranPelunasan.getTransactionId());
                                    data.put("no_rek", setoranPelunasan.getNoRekening());
                                    data.put("nama", setoranPelunasan.getNamaJemaah());
                                    data.put("status", setoranPelunasan.getStatusTransaksi().getNamaStatusTransaksi());
                                    data.put("nominal", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                    nominal+=setoranPelunasan.getNominalSetoran().doubleValue();
                                    data.put("biaya_admin", kursIndonesia.format(0D));
                                    biayaAdmin+=0;
                                    data.put("total", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                    total+=setoranPelunasan.getNominalSetoran().doubleValue();
                                    index++;
                                    dataList.add(data);
                                } else {
                                    continue;
                                }
                            }
                            outerDataPelunasan.put("data", dataList);
                            outerDataPelunasanList.add(outerDataPelunasan);
                        }
                    }
                    isiPelunasan.put("outer_data", outerDataPelunasanList);
                    isiPelunasan.put("tot_nominal", kursIndonesia.format(nominal));
                    isiPelunasan.put("tot_biaya", kursIndonesia.format(biayaAdmin));
                    isiPelunasan.put("tot_total", kursIndonesia.format(total));
                    isiList.add(isiPelunasan);
                }
                outerResult.put("isi", isiList);
                return outerResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}