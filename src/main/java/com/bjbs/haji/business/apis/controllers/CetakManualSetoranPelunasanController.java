package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.CetakSetoranPelunasanHajiData;
import com.bjbs.haji.business.apis.dtos.CetakSetoranPelunasanHajiResponse;
import com.bjbs.haji.business.apis.dtos.SetoranPelunasanDTO;
import com.bjbs.haji.business.models.Channel;
import com.bjbs.haji.business.models.SetoranPelunasan;
import com.bjbs.haji.business.models.TipeHaji;
import com.bjbs.haji.business.repositories.haji.ChannelRepository;
import com.bjbs.haji.business.repositories.haji.TipeHajiRepository;
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

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cetak-manual/setoran-pelunasan")
public class CetakManualSetoranPelunasanController extends HibernateReportController<SetoranPelunasan, SetoranPelunasanDTO> {

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    TipeHajiRepository tipeHajiRepository;

    String[] month = {"", "JANUARI", "FEBRUARI", "MARET", "APRIL", "MEI", "JUNI","JULI", "AGUSTUS", "SEPTEMBER", "OKTOBER", "NOVEMBER", "DESEMBER"};

    public CetakManualSetoranPelunasanController(StorageService storageService) {
        super(storageService, "CetakPelunasan");
    }

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<SetoranPelunasan, SetoranPelunasanDTO> dataSource) throws Exception {
        long tipeHajiId = Long.parseLong(dataSource.getRequestParameterValue("tipeHajiId"));
        String noPorsi = dataSource.getRequestParameterValue("noPorsi");
        String branchCode = dataSource.getRequestParameterValue("branchCode");
        String userBranchCode = dataSource.getRequestParameterValue("userBranchCode");

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        String url = urlSwitchingApp + "api/switching_haji/cetak_setoran_pelunasan";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CetakSetoranPelunasanHajiData cetakSetoranPelunasanHajiData = new CetakSetoranPelunasanHajiData();
        cetakSetoranPelunasanHajiData.setNoRekening("0");
        TipeHaji tipeHaji = tipeHajiRepository.findById(tipeHajiId).orElse(null);
        cetakSetoranPelunasanHajiData.setJenisHaji(tipeHaji.getKodeHaji());
        cetakSetoranPelunasanHajiData.setSettlementDate(new Date());
        Channel channel = channelRepository.findById(1L).orElse(null);
        cetakSetoranPelunasanHajiData.setMerchantType(channel.getKodeMerchant());
        cetakSetoranPelunasanHajiData.setTerminalId(userBranchCode);
        cetakSetoranPelunasanHajiData.setNomorPorsi(noPorsi);
        cetakSetoranPelunasanHajiData.setBranchCode(branchCode);

        HttpEntity<CetakSetoranPelunasanHajiData> request = new HttpEntity<CetakSetoranPelunasanHajiData>(cetakSetoranPelunasanHajiData, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        JSONObject objectResponse = new JSONObject(response);

        System.out.println("result from switching : " + objectResponse.toString());

        CetakSetoranPelunasanHajiResponse data = mapper.readValue(objectResponse.get("data").toString(), CetakSetoranPelunasanHajiResponse.class);

        Map<String, Object> outerResult = new HashMap<>();
        Map<String, Object> innerResult = new HashMap<>();

        innerResult.put("branch_name", data.getNamaCabangBank().toUpperCase());
        innerResult.put("branch_address", data.getAlamatCabangBank().toUpperCase());
        innerResult.put("kota", data.getKabupatenKota().trim().toUpperCase());
        innerResult.put("no_porsi", noPorsi);
        innerResult.put("bank", data.getNamaBank());

        innerResult.put("nama_nasabah", data.getNamaJemaah().toUpperCase());
        innerResult.put("nama_ayah", data.getNamaOrangTua().toUpperCase());
        innerResult.put("jenis_kelamin", (data.getJenisKelamin().equals("1"))?"PRIA":"WANITA");
        innerResult.put("tempat_lahir", data.getTempatLahir().toUpperCase());
        LocalDate tglLahir = new SimpleDateFormat("ddMMyyyy").parse(data.getTanggalLahir()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        innerResult.put("tgl_lahir", tglLahir.getDayOfMonth() + " " + month[tglLahir.getMonthValue()] + " " + tglLahir.getYear());
        innerResult.put("umur_tahun", data.getUmurTahun() + " THN");
        innerResult.put("umur_bulan", data.getUmurBulan() + " BLN");
        innerResult.put("alamat", data.getAlamat().toUpperCase());
        innerResult.put("kode_pos", data.getKodePos().toUpperCase());
        innerResult.put("kelurahan", data.getDesa().toUpperCase());
        innerResult.put("kecamatan", data.getKecamatan().toUpperCase());
        innerResult.put("kab_kota", data.getKabupatenKota().toUpperCase());
        innerResult.put("nama_provinsi", data.getProvinsi().toUpperCase());
        innerResult.put("embarkasi", data.getEmbarkasi().toUpperCase());
        innerResult.put("setoran_awal", kursIndonesia.format(new Double(data.getNilaiSetoranAwal()) / 100));
        innerResult.put("setoran_pelunasan", kursIndonesia.format(new BigInteger(data.getSisaPelunasan()).doubleValue()));
        innerResult.put("biaya_bpih", kursIndonesia.format(new Double(data.getBiayaBpih()) / 100));
        innerResult.put("terbilang", bilangx(new Double(data.getBiayaBpih()) / 100).toUpperCase()+" RUPIAH");
        LocalDate tanggal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        innerResult.put("tanggal", tanggal.getDayOfMonth() + " " + month[tanggal.getMonthValue()] + " " + tanggal.getYear());
        innerResult.put("masehi", data.getTahunPelunasanMasehi());
        innerResult.put("hijriyah", data.getTahunPelunasanHijriah());

        outerResult.put("isi", innerResult);
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
