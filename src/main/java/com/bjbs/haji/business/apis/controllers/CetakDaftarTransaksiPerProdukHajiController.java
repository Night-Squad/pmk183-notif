package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.controllers.utility.CurrencyCode;
import com.bjbs.haji.business.apis.controllers.utility.CurrencyUtil;
import com.bjbs.haji.business.apis.controllers.utility.DateTimeFormatterUtil;
import com.bjbs.haji.business.apis.controllers.utility.StreamUtil;
import com.bjbs.haji.business.models.Channel;
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
@RequestMapping("/api/cetak-daftar-transaksi-produk-haji")
public class CetakDaftarTransaksiPerProdukHajiController extends HibernateReportController<Object, Object> {

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Autowired
    public CetakDaftarTransaksiPerProdukHajiController(StorageService storageService) {
        super(storageService, "TransaksiPerProdukHaji");
    }

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<Object, Object> dataSource) throws Exception {


        Date tglAwal = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAwal"));
        Date tglAkhir = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAkhir"));
        String teller = dataSource.getRequestParameterValue("teller");
        long productId = Long.parseLong(dataSource.getRequestParameterValue("productId"));

        Date plusOneDay = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(tglAkhir);
        c.add(Calendar.DATE, 1);
        plusOneDay = c.getTime();

        Map<String, Object> header = new HashMap<>();
        Map<String, Object> outerResult = new HashMap<>();

        try {
            header.put("instansi", "SISKOHAT");
            if (productId == 1) {
                header.put("produk", "[BPIH] SETORAN AWAL");
            } else {
                header.put("produk", "[BPIH] SETORAN PELUNASAN");
            }

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

            if (productId == 1) {
                outerResult.put("produk", "[BPIH] SETORAN AWAL");

                List<SetoranAwal> listSetoranAwal = setoranAwalRepository.getListSetoranAwalPeriod(tglAwal, plusOneDay);

                if (!teller.equals("0")) {
                    listSetoranAwal = listSetoranAwal.stream()
                            .filter(s -> s.getCreatedBy().equals(teller))
                            .collect(Collectors.toList());

                    List<SetoranAwal> listStatusAwal = listSetoranAwal.stream()
                            .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getStatusTransaksiId()))
                            .collect( Collectors.toList());

                    List<Map<String, Object>> isiList = new ArrayList<>();
                    Map<String, Object> isi=new HashMap<>();

                    if (listSetoranAwal.size() > 0) {
                        isi.put("branch_code", listSetoranAwal.get(0).getBranchCode());
                        isi.put("user", teller);
                        List<Map<String, Object>> data1List = new ArrayList<>();
                        Double bayar=0D, biaya=0D, total=0D;
                        Double bayarUser=0D, biayaUser=0D, totalUser=0D;
                        int index = 1;
                        for (SetoranAwal status : listStatusAwal) {

                            Map<String, Object> data1=new HashMap<>();
                            data1.put("status", status.getStatusTransaksi().getNamaStatusTransaksi());

                            List<Map<String, Object>> data2List = new ArrayList<>();
                            Double bayarStatus=0D, biayaStatus=0D, totalStatus=0D;
                            for (SetoranAwal setoranAwal : listSetoranAwal) {
                                if (status.getStatusTransaksi().getStatusTransaksiId() == setoranAwal.getStatusTransaksi().getStatusTransaksiId()) {
                                    Map<String, Object> data2=new HashMap<>();
                                    data2.put("no", index);
                                    data2.put("no_arsip", setoranAwal.getTransactionId());
                                    data2.put("nim", setoranAwal.getNoRekening());
                                    data2.put("nama", setoranAwal.getNamaJemaah());
                                    data2.put("jumlah", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                    bayarStatus+=setoranAwal.getNominalSetoran().doubleValue();
                                    data2.put("biaya_admin", kursIndonesia.format(0D));
                                    biayaStatus+=0D;
                                    data2.put("total", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                    totalStatus+=setoranAwal.getNominalSetoran().doubleValue();
                                    index++;
                                    data2List.add(data2);
                                } else {
                                    continue;
                                }
                            }
                            data1.put("data2", data2List);
                            data1.put("jumlah_status", kursIndonesia.format(bayarStatus));
                            bayarUser+=bayarStatus;
                            data1.put("biaya_status", kursIndonesia.format(biayaStatus));
                            biayaUser+=biayaStatus;
                            data1.put("total_status", kursIndonesia.format(totalStatus));
                            totalUser+=totalStatus;
                            data1List.add(data1);
                        }

                        isi.put("data1", data1List);
                        isi.put("jumlah_user", kursIndonesia.format(bayarUser));
                        bayar+=bayarUser;
                        isi.put("biaya_user", kursIndonesia.format(biayaUser));
                        biaya+=biayaUser;
                        isi.put("total_user", kursIndonesia.format(totalUser));
                        total+=totalUser;
                        isiList.add(isi);

                        outerResult.put("isi", isiList);
                        outerResult.put("tot_bayar", kursIndonesia.format(bayar));
                        outerResult.put("tot_biaya", kursIndonesia.format(biaya));
                        outerResult.put("tot_total", kursIndonesia.format(total));
                        return outerResult;
                    }
                } else {
                    List<SetoranAwal> listUserAwal = listSetoranAwal.stream()
                            .filter(StreamUtil.distinctByKey(s -> s.getCreatedBy()))
                            .collect(Collectors.toList());

                    List<SetoranAwal> listBranchAwal = listUserAwal.stream()
                            .filter(StreamUtil.distinctByKey(s -> s.getBranchCode()))
                            .collect(Collectors.toList());

                    List<SetoranAwal> listStatusAwal = listSetoranAwal.stream()
                            .filter(StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getStatusTransaksiId()))
                            .collect(Collectors.toList());

                    List<Map<String, Object>> isiList = new ArrayList<>();

                    if (listSetoranAwal.size() > 0) {
                        Double bayar = 0D, biaya = 0D, total = 0D;
                        for (SetoranAwal branchAwal : listBranchAwal) {
                            for (SetoranAwal userAwal : listUserAwal) {
                                Double bayarUser = 0D, biayaUser = 0D, totalUser = 0D;
                                int index = 1;
                                Map<String, Object> isi = new HashMap<>();
                                List<Map<String, Object>> data1List = new ArrayList<>();
                                if (branchAwal.getBranchCode().equals(userAwal.getBranchCode())) {
                                    isi.put("branch_code", userAwal.getBranchCode());
                                    isi.put("user", userAwal.getCreatedBy());
                                } else {
                                    continue;
                                }
                                for (SetoranAwal status : listStatusAwal) {
                                    Map<String, Object> data1 = new HashMap<>();
                                    List<Map<String, Object>> data2List = new ArrayList<>();
                                    Double bayarStatus = 0D, biayaStatus = 0D, totalStatus = 0D;
                                    for (SetoranAwal setoranAwal : listSetoranAwal) {
                                        if (status.getStatusTransaksi().getStatusTransaksiId() == setoranAwal.getStatusTransaksi().getStatusTransaksiId()
                                        && setoranAwal.getCreatedBy().equals(userAwal.getCreatedBy())) {
                                            Map<String, Object> data2 = new HashMap<>();
                                            data2.put("no", index);
                                            data2.put("no_arsip", setoranAwal.getTransactionId());
                                            data2.put("nim", setoranAwal.getNoRekening());
                                            data2.put("nama", setoranAwal.getNamaJemaah());
                                            data2.put("jumlah", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                            bayarStatus += setoranAwal.getNominalSetoran().doubleValue();
                                            data2.put("biaya_admin", kursIndonesia.format(0D));
                                            biayaStatus += 0D;
                                            data2.put("total", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                            totalStatus += setoranAwal.getNominalSetoran().doubleValue();
                                            index++;
                                            data2List.add(data2);
                                        } else {
                                            continue;
                                        }
                                    }
                                    if (data2List.size() > 0) {
                                        data1.put("status", status.getStatusTransaksi().getNamaStatusTransaksi());
                                        data1.put("data2", data2List);
                                        data1.put("jumlah_status", kursIndonesia.format(bayarStatus));
                                        bayarUser += bayarStatus;
                                        data1.put("biaya_status", kursIndonesia.format(biayaStatus));
                                        biayaUser += biayaStatus;
                                        data1.put("total_status", kursIndonesia.format(totalStatus));
                                        totalUser += totalStatus;
                                        data1List.add(data1);
                                    }
                                }
                                isi.put("data1", data1List);
                                isi.put("jumlah_user", kursIndonesia.format(bayarUser));
                                bayar += bayarUser;
                                isi.put("biaya_user", kursIndonesia.format(biayaUser));
                                biaya += biayaUser;
                                isi.put("total_user", kursIndonesia.format(totalUser));
                                total += totalUser;
                                isiList.add(isi);
                            }
                        }
                        outerResult.put("isi", isiList);
                        outerResult.put("tot_bayar", kursIndonesia.format(bayar));
                        outerResult.put("tot_biaya", kursIndonesia.format(biaya));
                        outerResult.put("tot_total", kursIndonesia.format(total));
                        return outerResult;
                    }
                }
            } else {
                outerResult.put("produk", "[BPIH] SETORAN PELUNASAN");
                List<SetoranPelunasan> listSetoranPelunasan = setoranPelunasanRepository.getListSetoranPelunasanPeriod(tglAwal, plusOneDay);

                if (!teller.equals("0")) {
                    listSetoranPelunasan = listSetoranPelunasan.stream()
                            .filter(s -> s.getCreatedBy().equals(teller))
                            .collect(Collectors.toList());

                    List<SetoranPelunasan> listStatusPelunasan = listSetoranPelunasan.stream()
                            .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getStatusTransaksiId()))
                            .collect( Collectors.toList());

                    List<Map<String, Object>> isiList = new ArrayList<>();
                    Map<String, Object> isi=new HashMap<>();

                    if (listSetoranPelunasan.size() > 0) {
                        isi.put("branch_code", listSetoranPelunasan.get(0).getBranchCode());
                        isi.put("user", teller);
                        List<Map<String, Object>> data1List = new ArrayList<>();
                        Double bayar=0D, biaya=0D, total=0D;
                        Double bayarUser=0D, biayaUser=0D, totalUser=0D;
                        int index = 1;
                        for (SetoranPelunasan status : listStatusPelunasan) {

                            Map<String, Object> data1=new HashMap<>();
                            data1.put("status", status.getStatusTransaksi().getNamaStatusTransaksi());

                            List<Map<String, Object>> data2List = new ArrayList<>();
                            Double bayarStatus=0D, biayaStatus=0D, totalStatus=0D;
                            for (SetoranPelunasan setoranPelunasan : listSetoranPelunasan) {
                                if (status.getStatusTransaksi().getStatusTransaksiId() == setoranPelunasan.getStatusTransaksi().getStatusTransaksiId()) {
                                    Map<String, Object> data2=new HashMap<>();
                                    data2.put("no", index);
                                    data2.put("no_arsip", setoranPelunasan.getTransactionId());
                                    data2.put("nim", setoranPelunasan.getNoRekening());
                                    data2.put("nama", setoranPelunasan.getNamaJemaah());
                                    data2.put("jumlah", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                    bayarStatus+=setoranPelunasan.getNominalSetoran().doubleValue();
                                    data2.put("biaya_admin", kursIndonesia.format(0D));
                                    biayaStatus+=0D;
                                    data2.put("total", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                    totalStatus+=setoranPelunasan.getNominalSetoran().doubleValue();
                                    index++;
                                    data2List.add(data2);
                                } else {
                                    continue;
                                }
                            }
                            data1.put("data2", data2List);
                            data1.put("jumlah_status", kursIndonesia.format(bayarStatus));
                            bayarUser+=bayarStatus;
                            data1.put("biaya_status", kursIndonesia.format(biayaStatus));
                            biayaUser+=biayaStatus;
                            data1.put("total_status", kursIndonesia.format(totalStatus));
                            totalUser+=totalStatus;
                            data1List.add(data1);
                        }

                        isi.put("data1", data1List);
                        isi.put("jumlah_user", kursIndonesia.format(bayarUser));
                        bayar+=bayarUser;
                        isi.put("biaya_user", kursIndonesia.format(biayaUser));
                        biaya+=biayaUser;
                        isi.put("total_user", kursIndonesia.format(totalUser));
                        total+=totalUser;
                        isiList.add(isi);

                        outerResult.put("isi", isiList);
                        outerResult.put("tot_bayar", kursIndonesia.format(bayar));
                        outerResult.put("tot_biaya", kursIndonesia.format(biaya));
                        outerResult.put("tot_total", kursIndonesia.format(total));
                        return outerResult;
                    }
                } else {
                    List<SetoranPelunasan> listUserPelunasan = listSetoranPelunasan.stream()
                            .filter(StreamUtil.distinctByKey(s -> s.getCreatedBy()))
                            .collect(Collectors.toList());

                    List<SetoranPelunasan> listBranchPelunasan = listUserPelunasan.stream()
                            .filter(StreamUtil.distinctByKey(s -> s.getBranchCode()))
                            .collect(Collectors.toList());

                    List<SetoranPelunasan> listStatusPelunasan = listSetoranPelunasan.stream()
                            .filter(StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getStatusTransaksiId()))
                            .collect(Collectors.toList());

                    List<Map<String, Object>> isiList = new ArrayList<>();

                    if (listSetoranPelunasan.size() > 0) {
                        Double bayar = 0D, biaya = 0D, total = 0D;
                        for (SetoranPelunasan branchPelunasan : listBranchPelunasan) {
                            for (SetoranPelunasan userPelunasan : listUserPelunasan) {
                                Double bayarUser = 0D, biayaUser = 0D, totalUser = 0D;
                                int index = 1;
                                Map<String, Object> isi = new HashMap<>();
                                List<Map<String, Object>> data1List = new ArrayList<>();
                                if (branchPelunasan.getBranchCode().equals(userPelunasan.getBranchCode())) {
                                    isi.put("branch_code", userPelunasan.getBranchCode());
                                    isi.put("user", userPelunasan.getCreatedBy());
                                } else {
                                    continue;
                                }
                                for (SetoranPelunasan status : listStatusPelunasan) {
                                    Map<String, Object> data1 = new HashMap<>();
                                    List<Map<String, Object>> data2List = new ArrayList<>();
                                    Double bayarStatus = 0D, biayaStatus = 0D, totalStatus = 0D;
                                    for (SetoranPelunasan setoranPelunasan : listSetoranPelunasan) {
                                        if (status.getStatusTransaksi().getStatusTransaksiId() == setoranPelunasan.getStatusTransaksi().getStatusTransaksiId()
                                                && setoranPelunasan.getCreatedBy().equals(userPelunasan.getCreatedBy())) {
                                            Map<String, Object> data2 = new HashMap<>();
                                            data2.put("no", index);
                                            data2.put("no_arsip", setoranPelunasan.getTransactionId());
                                            data2.put("nim", setoranPelunasan.getNoRekening());
                                            data2.put("nama", setoranPelunasan.getNamaJemaah());
                                            data2.put("jumlah", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                            bayarStatus += setoranPelunasan.getNominalSetoran().doubleValue();
                                            data2.put("biaya_admin", kursIndonesia.format(0D));
                                            biayaStatus += 0D;
                                            data2.put("total", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                            totalStatus += setoranPelunasan.getNominalSetoran().doubleValue();
                                            index++;
                                            data2List.add(data2);
                                        } else {
                                            continue;
                                        }
                                    }
                                    if (data2List.size() > 0) {
                                        data1.put("status", status.getStatusTransaksi().getNamaStatusTransaksi());
                                        data1.put("data2", data2List);
                                        data1.put("jumlah_status", kursIndonesia.format(bayarStatus));
                                        bayarUser += bayarStatus;
                                        data1.put("biaya_status", kursIndonesia.format(biayaStatus));
                                        biayaUser += biayaStatus;
                                        data1.put("total_status", kursIndonesia.format(totalStatus));
                                        totalUser += totalStatus;
                                        data1List.add(data1);
                                    }
                                }
                                isi.put("data1", data1List);
                                isi.put("jumlah_user", kursIndonesia.format(bayarUser));
                                bayar += bayarUser;
                                isi.put("biaya_user", kursIndonesia.format(biayaUser));
                                biaya += biayaUser;
                                isi.put("total_user", kursIndonesia.format(totalUser));
                                total += totalUser;
                                isiList.add(isi);
                            }
                        }
                        outerResult.put("isi", isiList);
                        outerResult.put("tot_bayar", kursIndonesia.format(bayar));
                        outerResult.put("tot_biaya", kursIndonesia.format(biaya));
                        outerResult.put("tot_total", kursIndonesia.format(total));
                        return outerResult;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    }

}