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
@RequestMapping("/api/cetak-rekap-transaksi-haji")
public class CetakRekapTransaksiHaji extends HibernateReportController<Object, Object> {

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Autowired
    public CetakRekapTransaksiHaji(StorageService storageService) {
        super(storageService, "RekapTransaksiHaji");
    }

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<Object, Object> dataSource) throws Exception {

        Date tglAwal = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAwal"));
        Date tglAkhir = new SimpleDateFormat("dd/MM/yyyy").parse(dataSource.getRequestParameterValue("tglAkhir"));
        String teller = dataSource.getRequestParameterValue("teller");

        Date plusOneDay = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(tglAkhir);
        c.add(Calendar.DATE, 1);
        plusOneDay = c.getTime();

        Map<String, Object> outerResult = new HashMap<>();
        Map<String, Object> header = new HashMap<>();

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

            List<SetoranAwal> listSetoranAwal = setoranAwalRepository.getListSetoranAwalPeriod(tglAwal, plusOneDay);
            List<SetoranAwal> listStatus = listSetoranAwal.stream()
                    .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getNamaStatusTransaksi()))
                    .collect( Collectors.toList());

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
            Long counter=1L;
            for (SetoranAwal branchAwal : listBranchAwal) {
                for (SetoranAwal userAwal : listUserAwal) {
                    if (branchAwal.getBranchCode().equals(userAwal.getBranchCode())) {
                        for(SetoranAwal statusAwal : listStatus) {
                            Map<String, Object> isi=new HashMap<>();
                            List<Map<String, Object>> data1List=new ArrayList<>();
                            isi.put("user", userAwal.getCreatedBy());
                            isi.put("branch_code", userAwal.getBranchCode());
                            Map<String, Object> data1=new HashMap<>();
                            Double bayarUser=0D, biayaUser=0D, totalUser=0D;
                            data1.put("status", statusAwal.getStatusTransaksi().getNamaStatusTransaksi());
                            List<Map<String, Object>> data2List = new ArrayList<>();
                            Double bayarStatus=0D, biayaStatus=0D, totalStatus=0D;
                            int jumlah = 0;
                            for (SetoranAwal setoranAwal : listSetoranAwal) {
                                if (statusAwal.getStatusTransaksi().getStatusTransaksiId() == setoranAwal.getStatusTransaksi().getStatusTransaksiId()
                                        && setoranAwal.getCreatedBy().equals(userAwal.getCreatedBy())) {
                                    bayarStatus+=setoranAwal.getNominalSetoran().doubleValue();
                                    biayaStatus+=0;
                                    totalStatus+=setoranAwal.getNominalSetoran().doubleValue();
                                    jumlah++;
                                } else {
                                    continue;
                                }
                            }
                            Map<String, Object> data2=new HashMap<>();
                            data2.put("no", counter++);
                            data2.put("instansi", "SISKOHAT");
                            data2.put("produk", "[BPIH] SETORAN AWAL");
                            data2.put("jumlah", jumlah);
                            data2.put("bayar", kursIndonesia.format(bayarStatus));
                            data2.put("biaya_admin", kursIndonesia.format(biayaStatus));
                            data2.put("total", kursIndonesia.format(totalStatus));
                            data2List.add(data2);
                            data1.put("data2", data2List);
                            data1.put("bayar_status", kursIndonesia.format(bayarStatus));
                            bayarUser+=bayarStatus;
                            data1.put("biaya_status", kursIndonesia.format(biayaStatus));
                            biayaUser+=biayaStatus;
                            data1.put("total_status", kursIndonesia.format(totalStatus));
                            totalUser+=totalStatus;
                            data1List.add(data1);
                            isi.put("data1", data1List);
                            isi.put("bayar_user", kursIndonesia.format(bayarUser));
                            bayar+=bayarUser;
                            isi.put("biaya_user", kursIndonesia.format(biayaUser));
                            biaya+=biayaUser;
                            isi.put("total_user", kursIndonesia.format(totalUser));
                            total+=totalUser;
                            isiList.add(isi);
                        }
                    } else {
                        continue;
                    }
                }
            }

            List<SetoranPelunasan> listSetoranPelunasan = setoranPelunasanRepository.getListSetoranPelunasanPeriod(tglAwal, plusOneDay);
            List<SetoranPelunasan> listStatusPelunasan = listSetoranPelunasan.stream()
                    .filter( StreamUtil.distinctByKey(s -> s.getStatusTransaksi().getNamaStatusTransaksi()))
                    .collect( Collectors.toList());

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
                            List<Map<String, Object>> data1List=new ArrayList<>();
                            isi.put("user", userPelunasan.getCreatedBy());
                            isi.put("branch_code", userPelunasan.getBranchCode());

                            Map<String, Object> data1=new HashMap<>();
                            Double bayarUser=0D, biayaUser=0D, totalUser=0D;
                            data1.put("status", userPelunasan.getStatusTransaksi().getNamaStatusTransaksi());
                            List<Map<String, Object>> data2List = new ArrayList<>();
                            Double bayarStatus=0D, biayaStatus=0D, totalStatus=0D;
                            int jumlah = 0;
                            for (SetoranPelunasan setoranPelunasan : listSetoranPelunasan) {
                                if (statusPelunasan.getStatusTransaksi().getStatusTransaksiId() == setoranPelunasan.getStatusTransaksi().getStatusTransaksiId()
                                        && setoranPelunasan.getCreatedBy().equals(userPelunasan.getCreatedBy())) {
                                    bayarStatus+=setoranPelunasan.getNominalSetoran().doubleValue();
                                    biayaStatus+=0;
                                    totalStatus+=setoranPelunasan.getNominalSetoran().doubleValue();
                                    jumlah++;
                                } else {
                                    continue;
                                }
                            }
                            Map<String, Object> data2=new HashMap<>();
                            data2.put("no", counter++);
                            data2.put("instansi", "SISKOHAT");
                            data2.put("produk", "[BPIH] SETORAN PELUNASAN");
                            data2.put("jumlah", jumlah);
                            data2.put("bayar", kursIndonesia.format(bayarStatus));
                            data2.put("biaya_admin", kursIndonesia.format(biayaStatus));
                            data2.put("total", kursIndonesia.format(totalStatus));
                            data2List.add(data2);
                            data1.put("data2", data2List);
                            data1.put("bayar_status", kursIndonesia.format(bayarStatus));
                            bayarUser+=bayarStatus;
                            data1.put("biaya_status", kursIndonesia.format(biayaStatus));
                            biayaUser+=biayaStatus;
                            data1.put("total_status", kursIndonesia.format(totalStatus));
                            totalUser+=totalStatus;
                            data1List.add(data1);
                            isi.put("data1", data1List);
                            isi.put("bayar_user", kursIndonesia.format(bayarUser));
                            bayar+=bayarUser;
                            isi.put("biaya_user", kursIndonesia.format(biayaUser));
                            biaya+=biayaUser;
                            isi.put("total_user", kursIndonesia.format(totalUser));
                            total+=totalUser;
                            isiList.add(isi);
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




//        Map<String, Object> outerResult = new HashMap<>();
//
//        final LocalDate startDate=LocalDate.parse(dataSource.getRequestHeaderValue("start-date"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//        final LocalDate endDate=LocalDate.parse(dataSource.getRequestHeaderValue("end-date"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//
//        //* Header
//        Map<String, Object> header = new HashMap<>();
//
//        header.put("dari", DateTimeFormatterUtil.getNumericReadableDateFrom(startDate).replace("/", "-"));
//        header.put("sampai", DateTimeFormatterUtil.getNumericReadableDateFrom(endDate).replace("/", "-"));
//        header.put("teller", (dataSource.getRequestHeaderValue("teller")!=null && dataSource.getRequestHeaderValue("teller")!="0")?dataSource.getRequestHeaderValue("teller"):"ALL");
//
//        outerResult.put("header", header);
//        //*/
//
//        //dataSource.getRequestHeaderValue("")
//        //* Isi
//        List<Map<String, Object>> isiList = new ArrayList<>();
//
//        List<Long> branchIdList=new ArrayList<>(
//            (dataSource.getRequestHeaderValue("branch-id")!=null)?
//                Arrays.asList(Long.parseLong(dataSource.getRequestHeaderValue("branch-id"))): null
////                pbRepo.getExistingBranchId()
//        );
//
//        Double bayar=0D, biaya=0D, total=0D;
//        Long counter=1L;
//        for(Long branchId:branchIdList){
//            List<Long> statusIdList=new ArrayList<>(
////                    pbRepo.getExistingStatusIdByBranch(branchId)
//            );
//
//            for(Long statusId:statusIdList){
//                List<String> userList=new ArrayList<>(
//                    (dataSource.getRequestHeaderValue("teller")!=null)?
//                        Arrays.asList(dataSource.getRequestHeaderValue("teller")): null
////                        pbRepo.getExistingUserByStatusAndBranch(statusId, branchId)
//                );
//
//                for(String user:userList){
//                    Map<String, Object> isi=new HashMap<>();
//                    List<Map<String, Object>> data1List = new ArrayList<>();
//
////                    Branch branch=brRepo.findById(branchId).orElse(new Branch());
////                    isi.put("branch_code", (branch.getBranchCode()!=null)?branch.getBranchCode():"");
////                    isi.put("user", user);
//
//                    Double bayarUser=0D, biayaUser=0D, totalUser=0D;
//                    Map<String, Object> data1=new HashMap<>();
//                    List<Map<String, Object>> data2List = new ArrayList<>();
//
//                    StatusTransaksi statusTransaksi=stRepo.findById(statusId).orElse(new StatusTransaksi());
//                    data1.put("status", (statusTransaksi.getNamaStatusTransaksi()!=null)?statusTransaksi.getNamaStatusTransaksi():"");
//
//                    List<SetoranAwal> listObject=new ArrayList<>();
//                    LocalDate dateIndex=LocalDate.from(startDate);
//
//                    do{
////                        listObject.addAll(
////                            pbRepo.findAll(Example.of(new SetoranAwal(
////                                null, // long pembayaranAwalId,
////                                (dataSource.getRequestHeaderValue("channel-id")!=null)?new Channel(Long.parseLong(dataSource.getRequestHeaderValue("channel-id"))):null, // Channel channel,
////                                null, // KabKota kabKota,
////                                null, // MataUang mataUang,
////                                null, // Pekerjaan pekerjaan,
////                                null, // Pendidikan pendidikan,
////                                null, // StatusKawin statusKawin,
////                                (statusId!=null)?new StatusTransaksi((long) statusId):null, // StatusTransaksi statusTransaksi,
////                                null, // TipeHaji tipeHaji,
////                                 (branchId!=null)?branchId:null, // Long branchId,
////                                null, // String namaNasabah,
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
//                    Double bayarStatus=0D, biayaStatus=0D, totalStatus=0D;
//                    for(SetoranAwal object:listObject){
////                        bayarStatus+=object.getJumlahPembayaran().doubleValue();
//                        biayaStatus+=0;
////                        totalStatus+=object.getJumlahPembayaran().doubleValue();
//                    }
//                    Map<String, Object> data2=new HashMap<>();
//
//                    data2.put("no", counter++);
//                    data2.put("instansi", "PERGURUAN TINGGI (PEMBAYARAN)");
//                    data2.put("produk", "01311 - PEMBAYARAN UIN-SGD");
//                    data2.put("jumlah", data2List.size()+1);
//                    data2.put("bayar", CurrencyUtil.of(CurrencyCode.IDR).withValue(bayarStatus).asCurrencyString());
//                    data2.put("biaya_admin", CurrencyUtil.of(CurrencyCode.IDR).withValue(biayaStatus).asCurrencyString());
//                    data2.put("total", CurrencyUtil.of(CurrencyCode.IDR).withValue(totalStatus).asCurrencyString());
//
//                    data2List.add(data2);
//
//                    data1.put("data2", data2List);
//
//                    data1.put("bayar_status", CurrencyUtil.of(CurrencyCode.IDR).withValue(bayarStatus).asCurrencyString());
//                    bayarUser+=bayarStatus;
//                    data1.put("biaya_status", CurrencyUtil.of(CurrencyCode.IDR).withValue(biayaStatus).asCurrencyString());
//                    biayaUser+=biayaStatus;
//                    data1.put("total_status", CurrencyUtil.of(CurrencyCode.IDR).withValue(totalStatus).asCurrencyString());
//                    totalUser+=totalStatus;
//
//                    data1List.add(data1);
//
//                    isi.put("data1", data1List);
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
//
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