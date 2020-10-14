package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.controllers.utility.CurrencyCode;
import com.bjbs.haji.business.apis.controllers.utility.CurrencyUtil;
import com.bjbs.haji.business.apis.dtos.*;
import com.bjbs.haji.business.models.MataUang;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.SetoranPelunasan;
import com.bjbs.haji.business.repositories.haji.MataUangRepository;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.iona.springboot.controllers.HibernateReportController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.io.iona.springboot.sources.HibernateDataUtility;
import com.io.iona.springboot.storage.StorageService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cetak-resi")
public class CetakResiController extends HibernateReportController<SetoranAwal, SetoranAwalDTO> {

    public CetakResiController(StorageService storageService) {
        super(storageService, "CetakResi");
    }

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<SetoranAwal, SetoranAwalDTO> dataSource) throws Exception {

        String noValidasi = dataSource.getRequestParameterValue("noValidasi");
        String noPorsi =  dataSource.getRequestParameterValue("noPorsi");
        String branchCode = dataSource.getRequestParameterValue("branchCode");
        String branchName = dataSource.getRequestParameterValue("branchName");
        long productId = Long.parseLong(dataSource.getRequestParameterValue("productId"));

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        Map<String, Object> outerResult = new HashMap<>();

        if (productId == 1) {
            SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoValidasi(noValidasi);
            if (setoranAwal != null) {
                Map<String, Object> innerResult = new HashMap<>();
                innerResult.put("product", "SETORAN AWAL");
                innerResult.put("attribute", "No. Validasi");
                innerResult.put("tanggalTransaksi", new SimpleDateFormat("dd/MM/yyyy").format(setoranAwal.getTanggalTransaksi()));
                innerResult.put("jamTransaksi", new SimpleDateFormat("HH:mm:ss").format(setoranAwal.getTanggalTransaksi()));
                innerResult.put("kodeNamaBank", "425 - BANK JABAR BANTEN SYARIAH" );
                innerResult.put("kodeNamaCabangBank", branchCode + " - " + branchName.toUpperCase());
                innerResult.put("transactionId", setoranAwal.getTransactionId() != null ? setoranAwal.getTransactionId() : "");
                innerResult.put("channel", setoranAwal.getCreatedBy());
                innerResult.put("terminalId", branchCode + setoranAwal.getCreatedBy());
                innerResult.put("noRekening", setoranAwal.getNoRekening());
                innerResult.put("namaRekening", (setoranAwal.getNamaJemaah()!=null)?setoranAwal.getNamaJemaah().trim().toUpperCase():"");
                innerResult.put("mataUang", (setoranAwal.getMataUang().getKodeMataUang() + " - " + setoranAwal.getMataUang().getMataUang() + " - " + setoranAwal.getMataUang().getDescription()).toUpperCase());
                innerResult.put("noValidasi", noValidasi);
                innerResult.put("namaCalhaj", (setoranAwal.getNamaJemaah()!=null)?setoranAwal.getNamaJemaah().trim().toUpperCase():"");
                innerResult.put("nilaiSetoran", kursIndonesia.format(setoranAwal.getNominalSetoran()));
                innerResult.put("terbilang", bilangx(setoranAwal.getNominalSetoran().doubleValue()).toUpperCase() + " RUPIAH");
                outerResult.put("isi", innerResult);
            }
        } else {
            SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.getSetoranPelunasanByNoPorsi(noPorsi);
            if (setoranPelunasan != null) {
                Map<String, Object> innerResult = new HashMap<>();
                innerResult.put("product", "SETORAN PELUNASAN");
                innerResult.put("attribute", "No. Porsi");
                innerResult.put("tanggalTransksi", new SimpleDateFormat("dd/MM/yyyy").format(setoranPelunasan.getTanggalTransaksi()));
                innerResult.put("jamTransaksi", new SimpleDateFormat("HH:mm:ss").format(setoranPelunasan.getTanggalTransaksi()));
                innerResult.put("kodeNamaBank", "425 - BANK JABAR BANTEN SYARIAH");
                innerResult.put("kodeNamaCabangBank", branchCode + " - " + branchName);
                innerResult.put("transactionId", setoranPelunasan.getTransactionId() != null ? setoranPelunasan.getTransactionId() : "");
                innerResult.put("channel", setoranPelunasan.getCreatedBy());
                innerResult.put("terminalId", branchCode + setoranPelunasan.getCreatedBy());
                innerResult.put("noRekening", setoranPelunasan.getNoRekening());
                innerResult.put("namaRekening", (setoranPelunasan.getNamaJemaah() != null) ? setoranPelunasan.getNamaJemaah().trim().toUpperCase() : "");
                innerResult.put("mataUang", (setoranPelunasan.getMataUang().getKodeMataUang() + " - " + setoranPelunasan.getMataUang().getMataUang() + " - " + setoranPelunasan.getMataUang().getDescription()).toUpperCase());
                innerResult.put("noValidasi", noPorsi);
                innerResult.put("namaCalhaj", (setoranPelunasan.getNamaJemaah() != null) ? setoranPelunasan.getNamaJemaah().trim().toUpperCase() : "");
                innerResult.put("nilaiSetoran", kursIndonesia.format(setoranPelunasan.getNominalSetoran()));
                innerResult.put("terbilang", bilangx(setoranPelunasan.getNominalSetoran().doubleValue()).toUpperCase() + " RUPIAH");
                outerResult.put("isi", innerResult);
            }
        }
        return outerResult;
    }

    String[] nomina={"","Satu","Dua","Tiga","Empat","Lima","Enam",
            "Tujuh","Delapan","Sembilan","Sepuluh","Sebelas"};

    public String bilangx(double angka) {
        if (angka < 12) {
            return nomina[(int) angka];
        }

        if (angka >= 12 && angka <= 19) {
            return nomina[(int) angka % 10] + " Belas ";
        }

        if (angka >= 20 && angka <= 99) {
            return nomina[(int) angka / 10] + " Puluh " + nomina[(int) angka % 10];
        }

        if (angka >= 100 && angka <= 199) {
            return "Seratus " + bilangx(angka % 100);
        }

        if (angka >= 200 && angka <= 999) {
            return nomina[(int) angka / 100] + " Ratus " + bilangx(angka % 100);
        }

        if (angka >= 1000 && angka <= 1999) {
            return "Seribu " + bilangx(angka % 1000);
        }

        if (angka >= 2000 && angka <= 999999) {
            return bilangx((int) angka / 1000) + " Ribu " + bilangx(angka % 1000);
        }

        if (angka >= 1000000 && angka <= 999999999) {
            return bilangx((int) angka / 1000000) + " Juta " + bilangx(angka % 1000000);
        }

        return "";
    }
}
