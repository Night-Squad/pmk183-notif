package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.io.iona.springboot.controllers.HibernateReportController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.io.iona.springboot.sources.HibernateDataUtility;
import com.io.iona.springboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cetak-resi-setoran-awal-2")
public class CetakResiSetoranAwal2Controller extends HibernateReportController<SetoranAwal, SetoranAwalDTO> {

    public CetakResiSetoranAwal2Controller(StorageService storageService) {
        super(storageService, "CetakResiV2");
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

        String noValidasi = dataSource.getRequestParameterValue("setoranAwalId");

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        Map<String, Object> outerResult = new HashMap<>();
        SetoranAwal setoranAwal = setoranAwalRepository.findById(Long.parseLong(noValidasi)).orElse(null);
        if (setoranAwal != null) {
            System.out.println("****************************GENERATING RESI SETORAN AWAL*****************************");
            System.out.println("Setoran Awal Id : " + setoranAwal.getSetoranAwalId());
            System.out.println("No Validasi : " + setoranAwal.getNoValidasi());
            System.out.println("Nama Jemaah : " + setoranAwal.getNamaJemaah());
            System.out.println("*************************************************************************************");
            Map<String, Object> innerResult = new HashMap<>();

            StringBuilder accountNumber = new StringBuilder();
            int limit = setoranAwal.getNoRekening().length() - 4;
            for (int i = 0; i < limit; i++) {
                accountNumber.append("x");
            }
            accountNumber.append(setoranAwal.getNoRekening().substring(limit));

            StringBuilder identityNumber = new StringBuilder();
            limit = setoranAwal.getNoIdentitas().length() - 4;
            for (int i = 0; i < limit; i++) {
                identityNumber.append("x");
            }
            identityNumber.append(setoranAwal.getNoIdentitas().substring(limit));

            innerResult.put("nomorVirtualAccount", setoranAwal.getVirtualAccount());
            innerResult.put("nomorKtp", identityNumber.toString());
            innerResult.put("tanggalTransaksi", new SimpleDateFormat("dd/MM/yyyy").format(setoranAwal.getTanggalTransaksi()));
            innerResult.put("jamTransaksi", new SimpleDateFormat("HH:mm:ss").format(setoranAwal.getTanggalTransaksi()));
            innerResult.put("noRekening", accountNumber.toString());
            innerResult.put("noValidasi", setoranAwal.getNoValidasi());
            innerResult.put("namaJemaahHaji", (setoranAwal.getNamaJemaah()!=null)?setoranAwal.getNamaJemaah().trim().toUpperCase():"");
            innerResult.put("jumlah", kursIndonesia.format(setoranAwal.getNominalSetoran().toBigInteger()));
            innerResult.put("terbilang", bilangx(setoranAwal.getNominalSetoran().doubleValue()).toUpperCase() + " RUPIAH");
            StringBuilder qrCodeValue = new StringBuilder();
            qrCodeValue.append("Nomor Validasi : ").append(noValidasi).append(" \n")
                    .append("Nomor Virtual Account : ").append(setoranAwal.getVirtualAccount()).append(" \n")
                    .append("Tanggal Transaksi : ").append(new SimpleDateFormat("dd/MM/yyyy").format(setoranAwal.getTanggalTransaksi())).append(" ")
                    .append(new SimpleDateFormat("HH:mm:ss").format(setoranAwal.getTanggalTransaksi())).append(" \n")
                    .append("Nomor Rekening : ").append(accountNumber.toString()).append(" \n")
                    .append("Nomor KTP : ").append(identityNumber.toString()).append(" \n")
                    .append("Nama Jemaah Haji : ").append((setoranAwal.getNamaJemaah()!=null)?setoranAwal.getNamaJemaah().trim().toUpperCase():"").append(" \n")
                    .append("Jumlah : ").append("Rp ").append(kursIndonesia.format(setoranAwal.getNominalSetoran().toBigInteger())).append(",-").append(" \n")
                    .append("Terbilang : ").append(bilangx(setoranAwal.getNominalSetoran().doubleValue()).toUpperCase()).append(" RUPIAH \n");
            innerResult.put("qrCode", qrCodeValue.toString());
            outerResult.put("isi", innerResult);
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
