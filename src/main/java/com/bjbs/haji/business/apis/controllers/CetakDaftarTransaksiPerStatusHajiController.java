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
@RequestMapping("/api/cetak-daftar-transaksi-status-haji")
public class CetakDaftarTransaksiPerStatusHajiController extends HibernateReportController<Object, Object> {

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    StatusTransaksiRepository statusTransaksiRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Autowired
    public CetakDaftarTransaksiPerStatusHajiController(StorageService storageService) {
        super(storageService, "TransaksiPerStatusHaji");
    }

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<Object, Object> dataSource) throws Exception {

        Date tglAwal = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAwal"));
        Date tglAkhir = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAkhir"));
        String teller = dataSource.getRequestParameterValue("teller");
        long statusId = Long.parseLong(dataSource.getRequestParameterValue("statusId"));

        Map<String, Object> outerResult = new HashMap<>();
        Map<String, Object> header = new HashMap<>();

        try {
            header.put("dari", new SimpleDateFormat("dd-MM-yyyy").format(tglAwal));
            header.put("sampai", new SimpleDateFormat("dd-MM-yyyy").format(tglAkhir));
            header.put("teller", teller.equals("0") ? "ALL" : teller);
            StatusTransaksi statusTransaksi = statusTransaksiRepository.findById(statusId).orElse(null);
            header.put("status", statusId == 0 ? "ALL Status" : statusTransaksi.getNamaStatusTransaksi());
            outerResult.put("header", header);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            List<SetoranAwal> listSetoranAwal = setoranAwalRepository.getListSetoranAwalPeriod(tglAwal, tglAkhir);
            List<SetoranAwal> listStatus = listSetoranAwal.stream()
                    .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getNamaStatusTransaksi()))
                    .collect( Collectors.toList());

            if (statusId != 0) {
                listSetoranAwal = listSetoranAwal.stream()
                        .filter(s -> s.getStatusTransaksi().getStatusTransaksiId() == statusId)
                        .collect(Collectors.toList());
            }

            List<SetoranAwal> listUserAwal = listSetoranAwal.stream()
                    .filter(StreamUtil.distinctByKey(s -> s.getCreatedBy()))
                    .collect(Collectors.toList());

            List<SetoranAwal> listBranchAwal = listUserAwal.stream()
                    .filter(StreamUtil.distinctByKey(s -> s.getBranchCode()))
                    .collect(Collectors.toList());

            if (!teller.equals("0")) {
                listSetoranAwal = listSetoranAwal.stream()
                        .filter(s -> s.getCreatedBy().equals(teller))
                        .collect(Collectors.toList());
            }

            List<Map<String, Object>> isiList = new ArrayList<>();
            Double bayar=0D, biaya=0D, total=0D;
            for (SetoranAwal branchAwal : listBranchAwal) {
                for (SetoranAwal userAwal : listUserAwal) {
                    if (branchAwal.getBranchCode().equals(userAwal.getBranchCode())) {
                        for(SetoranAwal statusAwal : listStatus) {
                            Map<String, Object> isi=new HashMap<>();
                            List<Map<String, Object>> dataList=new ArrayList<>();
                            isi.put("user", userAwal.getCreatedBy());
                            isi.put("branch_code", userAwal.getBranchCode());
                            isi.put("status", statusAwal.getStatusTransaksi().getNamaStatusTransaksi());
                            Double bayarUser=0D, biayaUser=0D, totalUser=0D;
                            for (SetoranAwal setoranAwal : listSetoranAwal) {
                                if (statusAwal.getStatusTransaksi().getStatusTransaksiId() == setoranAwal.getStatusTransaksi().getStatusTransaksiId()
                                        && setoranAwal.getCreatedBy().equals(userAwal.getCreatedBy())) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("no", dataList.size() + 1);
                                    data.put("no_arsip", setoranAwal.getTransactionId());
                                    data.put("nim", setoranAwal.getNoRekening());
                                    data.put("nama", setoranAwal.getNamaJemaah());
                                    data.put("bayar", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                    bayarUser += setoranAwal.getNominalSetoran().doubleValue();
                                    data.put("biaya_admin", kursIndonesia.format(0D));
                                    biayaUser += 0D;
                                    data.put("total", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                                    totalUser += setoranAwal.getNominalSetoran().doubleValue();
                                    dataList.add(data);
                                } else {
                                    continue;
                                }
                            }
                            if (dataList.size() > 0) {
                                isi.put("produk", "[BPIH] SETORAN AWAL");
                                isi.put("data", dataList);
                                isi.put("bayar_user", kursIndonesia.format(bayarUser));
                                bayar+=bayarUser;
                                isi.put("biaya_user", kursIndonesia.format(biayaUser));
                                biaya+=biayaUser;
                                isi.put("total_user", kursIndonesia.format(totalUser));
                                total+=totalUser;
                                isiList.add(isi);
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }

            List<SetoranPelunasan> listSetoranPelunasan = setoranPelunasanRepository.getListSetoranPelunasanPeriod(tglAwal, tglAkhir);
            List<SetoranPelunasan> listStatusPelunasan = listSetoranPelunasan.stream()
                    .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getNamaStatusTransaksi()))
                    .collect( Collectors.toList());

            if (statusId != 0) {
                listSetoranPelunasan = listSetoranPelunasan.stream()
                        .filter(s -> s.getStatusTransaksi().getStatusTransaksiId() == statusId)
                        .collect(Collectors.toList());
            }

            List<SetoranPelunasan> listUserPelunasan = listSetoranPelunasan.stream()
                    .filter(StreamUtil.distinctByKey(s -> s.getCreatedBy()))
                    .collect(Collectors.toList());

            List<SetoranPelunasan> listBranchPelunasan = listUserPelunasan.stream()
                    .filter(StreamUtil.distinctByKey(s -> s.getBranchCode()))
                    .collect(Collectors.toList());

            if (!teller.equals("0")) {
                listSetoranPelunasan = listSetoranPelunasan.stream()
                        .filter(s -> s.getCreatedBy().equals(teller))
                        .collect(Collectors.toList());
            }

            for (SetoranPelunasan branchPelunasan : listBranchPelunasan) {
                for (SetoranPelunasan userPelunasan : listUserPelunasan) {
                    if (branchPelunasan.getBranchCode().equals(userPelunasan.getBranchCode())) {
                        for(SetoranPelunasan statusPelunasan : listStatusPelunasan) {
                            Map<String, Object> isi=new HashMap<>();
                            List<Map<String, Object>> dataList=new ArrayList<>();
                            isi.put("user", userPelunasan.getCreatedBy());
                            isi.put("branch_code", userPelunasan.getBranchCode());
                            isi.put("status", statusPelunasan.getStatusTransaksi().getNamaStatusTransaksi());
                            Double bayarUser=0D, biayaUser=0D, totalUser=0D;
                            for (SetoranPelunasan setoranPelunasan : listSetoranPelunasan) {
                                if (statusPelunasan.getStatusTransaksi().getStatusTransaksiId() == setoranPelunasan.getStatusTransaksi().getStatusTransaksiId()
                                        && setoranPelunasan.getCreatedBy().equals(userPelunasan.getCreatedBy())) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("no", dataList.size() + 1);
                                    data.put("no_arsip", setoranPelunasan.getTransactionId());
                                    data.put("nim", setoranPelunasan.getNoRekening());
                                    data.put("nama", setoranPelunasan.getNamaJemaah());
                                    data.put("bayar", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                    bayarUser += setoranPelunasan.getNominalSetoran().doubleValue();
                                    data.put("biaya_admin", kursIndonesia.format(0D));
                                    biayaUser += 0D;
                                    data.put("total", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                                    totalUser += setoranPelunasan.getNominalSetoran().doubleValue();
                                    dataList.add(data);
                                } else {
                                    continue;
                                }
                            }
                            if (dataList.size() > 0) {
                                isi.put("produk", "[BPIH] SETORAN PELUNASAN");
                                isi.put("data", dataList);
                                isi.put("bayar_user", kursIndonesia.format(bayarUser));
                                bayar+=bayarUser;
                                isi.put("biaya_user", kursIndonesia.format(biayaUser));
                                biaya+=biayaUser;
                                isi.put("total_user", kursIndonesia.format(totalUser));
                                total+=totalUser;
                                isiList.add(isi);
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }

            outerResult.put("isi", isiList);
            outerResult.put("tot_bayar", kursIndonesia.format(bayar));
            outerResult.put("tot_biaya", kursIndonesia.format(biaya));
            outerResult.put("tot_total", kursIndonesia.format(total));
            return outerResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
//
//
//
//        if(dataSource.getRequestHeaderValue("status-id")!=null && dataSource.getRequestHeaderValue("status-id")!="0"){
//            StatusTransaksi statusTransaksi=stRepo.findById(Long.parseLong(dataSource.getRequestHeaderValue("status-id"))).orElse(new StatusTransaksi());
//            header.put("status", (statusTransaksi.getNamaStatusTransaksi()!=null)?statusTransaksi.getNamaStatusTransaksi():"");
//        }else{
//            header.put("status", "ALL");
//        }
//        if(dataSource.getRequestHeaderValue("branch-id")!=null && dataSource.getRequestHeaderValue("branch-id")!="0"){
////            Branch branch=brRepo.findById(Long.parseLong(dataSource.getRequestHeaderValue("branch-id"))).orElse(new Branch());
////            header.put("branch_name", (branch.getBranchName()!=null)?branch.getBranchName():"");
//        }else{
//            header.put("branch_name", "ALL");
//        }
//
//        outerResult.put("header", header);
//        //*/
//
//        outerResult.put("produk", "01311 - PEMBAYARAN UIN-SGD");
//
//        //dataSource.getRequestHeaderValue("")
//        //* Isi
//        List<Map<String, Object>> isiList = new ArrayList<>();
//
//        List<Long> statusIdList=new ArrayList<>(
//            (dataSource.getRequestHeaderValue("status-id")!=null)?
//                Arrays.asList(Long.parseLong(dataSource.getRequestHeaderValue("status-id"))): null
////                paRepo.getExistingStatusId()
//        );
//
//        Double bayar=0D, biaya=0D, total=0D;
//        for(Long statusId:statusIdList){
//
//            List<Long> branchIdList=new ArrayList<>(
//                (dataSource.getRequestHeaderValue("branch-id")!=null)?
//                    Arrays.asList(Long.parseLong(dataSource.getRequestHeaderValue("branch-id"))): null
////                    paRepo.getExistingBranchIdByStatus(statusId)
//            );
//
//            for(Long branchId:branchIdList){
//                List<String> userList=new ArrayList<>(
////                        paRepo.getExistingUserByStatusAndBranch(statusId, branchId)
//                );
//
//                for(String user:userList){
//                    Map<String, Object> isi=new HashMap<>();
//                    List<Map<String, Object>> dataList=new ArrayList<>();
//
//                    StatusTransaksi statusTransaksi=stRepo.findById(statusId).orElse(new StatusTransaksi());
//                    isi.put("status", (statusTransaksi.getNamaStatusTransaksi()!=null)?statusTransaksi.getNamaStatusTransaksi():"");
////                    Branch branch=brRepo.findById(branchId).orElse(new Branch());
////                    isi.put("branch_code", (branch.getBranchCode()!=null)?branch.getBranchCode():"");
//                    isi.put("user", user);
//                    isi.put("produk", "01311 - PEMBAYARAN UIN-SGD");
//
//                    List<SetoranAwal> listObject=new ArrayList<>();
//                    LocalDate dateIndex=LocalDate.from(startDate);
//
//                    do{
////                        listObject.addAll(
////                            paRepo.findAll(Example.of(new SetoranAwal(
////                                null, // long pembayaranAwalId,
////                                (dataSource.getRequestHeaderValue("channel-id")!=null)?new Channel(Long.parseLong(dataSource.getRequestHeaderValue("channel-id"))):null, // Channel channel,
////                                null, // KabKota kabKota,
////                                null, // MataUang mataUang,
////                                null, // Pekerjaan pekerjaan,
////                                null, // Pendidikan pendidikan,
////                                null, // StatusKawin statusKawin,
////                                null, // StatusTransaksi statusTransaksi,
////                                null, // TipeHaji tipeHaji,
////                                (branchId!=null)?branchId:null, // Long branchId,
////                                null, // String namaJemaah,
////                                null, // String noIdentitas,
////                                null, // Date tglLahir,
////                                null, // String tempatLahir,
////                                null, // Short jenisKelamin,
////                                null, // String alamat,
////                                null, // String kodePos,
////                                null, // String kelurahan,
////                                null, // String kecamatan,
////                                null, // String namaAyah,
////                                null, // String noRekening,
////                                null, // BigDecimal jumlahPembayaran,
////                                null, // String noValidasi,
////                                Date.from(dateIndex.atStartOfDay(ZoneId.systemDefault()).toInstant()), // Date tglTransaksi,
////                                null, // Date jamTransaksi,
////                                null, // String retRefNumber,
////                                null, // Long systemTraceNumber,
////                                null, // String virtualAccount,
////                                null, // String embarkasi,
////                                null, // String kloter,
////                                null, // String idTerminal,
////                                null, // Date createdDate,
////                                (user!=null)?user:null, // String createdBy,
////                                null, // Date updatedDate,
////                                null, // String updatedBy,
////                                null, // String noArsip,
////                                null // Set<PelunasanHaji> pelunasanHajis
////                            )))
////                        );
//
//                        dateIndex=dateIndex.plusDays(1);
//                    }while(!dateIndex.equals(endDate));
//
//                    Double bayarUser=0D, biayaUser=0D, totalUser=0D;
//                    for(SetoranAwal object:listObject){
//                        Map<String, Object> data=new HashMap<>();
//
//                        data.put("no", dataList.size()+1);
//                        data.put("no_arsip", object.getNoArsip());
//                        data.put("nim", object.getNoRekening());
//                        data.put("nama", object.getNamaJemaah());
////                        data.put("bayar", CurrencyUtil.of(CurrencyCode.IDR).withValue(object.getJumlahPembayaran()).asCurrencyString());
////                        bayarUser+=object.getJumlahPembayaran().doubleValue();
//                        data.put("biaya_admin", CurrencyUtil.of(CurrencyCode.IDR).withValue(0D).asCurrencyString());
//                        biayaUser+=0D;
////                        data.put("total", CurrencyUtil.of(CurrencyCode.IDR).withValue(object.getJumlahPembayaran()).asCurrencyString());
////                        totalUser+=object.getJumlahPembayaran().doubleValue();
//
//                        dataList.add(data);
//                    }
//
//                    isi.put("data", dataList);
//
//                    isi.put("bayar_user", CurrencyUtil.of(CurrencyCode.IDR).withValue(bayarUser).asCurrencyString());
//                    bayar+=bayarUser;
//                    isi.put("biaya_user", CurrencyUtil.of(CurrencyCode.IDR).withValue(biayaUser).asCurrencyString());
//                    biaya+=biayaUser;
//                    isi.put("total_user", CurrencyUtil.of(CurrencyCode.IDR).withValue(totalUser).asCurrencyString());
//                    total+=totalUser;
//
//                    isiList.add(isi);
//                }
//            }
//        }
//
//        outerResult.put("isi", isiList);
//        outerResult.put("tot_bayar", CurrencyUtil.of(CurrencyCode.IDR).withValue(bayar).asCurrencyString());
//        outerResult.put("tot_biaya", CurrencyUtil.of(CurrencyCode.IDR).withValue(biaya).asCurrencyString());
//        outerResult.put("tot_total", CurrencyUtil.of(CurrencyCode.IDR).withValue(total).asCurrencyString());
//        //*/
//
//        //innerResult.put("", object.get());
        return null;
        
    }

}