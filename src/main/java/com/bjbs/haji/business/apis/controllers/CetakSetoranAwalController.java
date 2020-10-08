package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.controllers.utility.CurrencyCode;
import com.bjbs.haji.business.apis.controllers.utility.CurrencyUtil;
import com.bjbs.haji.business.apis.dtos.CetakSetoranAwalHajiData;
import com.bjbs.haji.business.apis.dtos.CetakSetoranAwalHajiResponse;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/cetak-setoran-awal")
public class CetakSetoranAwalController extends HibernateReportController<SetoranAwal, SetoranAwalDTO> {

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    String[] month = {"", "JANUARI", "FEBRUARI", "MARET", "APRIL", "MEI", "JUNI","JULI", "AGUSTUS", "SEPTEMBER", "OKTOBER", "NOVEMBER", "DESEMBER"};

    @Autowired
    public CetakSetoranAwalController(StorageService storageService) {
        super(storageService, "CetakSetoranAwal");
    }

    @Override
    public Map<String, Object> getData(HibernateDataUtility dataUtility, HibernateDataSource<SetoranAwal, SetoranAwalDTO> dataSource) throws Exception {

        long setoranAwalId = Long.parseLong(dataSource.getRequestParameterValue("setoranAwalId"));
        String branchCode = dataSource.getRequestParameterValue("branchCode");

        SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranAwalId).orElse(null);

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        String url = urlSwitchingApp + "api/switching_haji/cetak_setoran_awal";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CetakSetoranAwalHajiData cetakSetoranAwalHajiData = new CetakSetoranAwalHajiData();
        cetakSetoranAwalHajiData.setNoRekening(setoranAwal.getNoRekening());
        cetakSetoranAwalHajiData.setJenisHaji(setoranAwal.getTipeHaji().getKodeHaji());
        cetakSetoranAwalHajiData.setSettlementDate(new Date());
        cetakSetoranAwalHajiData.setMerchantType(setoranAwal.getChannel().getKodeMerchant());
        cetakSetoranAwalHajiData.setTerminalId(setoranAwal.getTerminalId());
        cetakSetoranAwalHajiData.setTanggalTransaksi(new SimpleDateFormat("ddMMyyyy").format(setoranAwal.getTanggalTransaksi()));
        cetakSetoranAwalHajiData.setNomorValidasi(setoranAwal.getNoValidasi());
        cetakSetoranAwalHajiData.setBranchCode(branchCode);

        HttpEntity<CetakSetoranAwalHajiData> request = new HttpEntity<CetakSetoranAwalHajiData>(cetakSetoranAwalHajiData, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        JSONObject objectResponse = new JSONObject(response);

        System.out.println("result from switching : " + objectResponse.toString());

        CetakSetoranAwalHajiResponse data = mapper.readValue(objectResponse.get("data").toString(), CetakSetoranAwalHajiResponse.class);

        Map<String, Object> outerResult = new HashMap<>();
        Map<String, Object> innerResult = new HashMap<>();

        innerResult.put("branch_name", (data.getNamaCabangBank()!=null) ?data.getNamaCabangBank().trim().toUpperCase():"");
        innerResult.put("branch_address", (data.getAlamatCabangBank()!=null)?data.getAlamatCabangBank().trim().toUpperCase():"");
        innerResult.put("kota", (data.getKabupatenKota()!=null)?data.getKabupatenKota().trim().toUpperCase():"");
        innerResult.put("no_validasi", setoranAwal.getNoValidasi());
        innerResult.put("bank", data.getNamaBank().trim().toUpperCase());
        innerResult.put("no_rekening", setoranAwal.getNoRekening());
        innerResult.put("nama_nasabah", (data.getNamaJemaah()!=null)?data.getNamaJemaah().trim().toUpperCase():"");
        innerResult.put("jenis_kelamin", (data.getJenisKelamin().equals("1"))?"PRIA":"WANITA");
        innerResult.put("tempat_lahir", (data.getTempatLahir()!=null)?data.getTempatLahir().trim().toUpperCase():"");
        LocalDate tglLahir = new SimpleDateFormat("ddMMyyyy").parse(data.getTanggalLahir()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        innerResult.put("tgl_lahir", tglLahir.getDayOfMonth() + " " + month[tglLahir.getMonthValue()] + " " + tglLahir.getYear());
        innerResult.put("alamat", (data.getAlamat()!=null)?data.getAlamat().trim().toUpperCase():"");
        innerResult.put("kode_pos", (data.getKodePos()!=null)?data.getKodePos().toUpperCase():"");
        innerResult.put("kelurahan", (data.getDesa()!=null)?data.getDesa().trim().toUpperCase():"");
        innerResult.put("kecamatan", (data.getKecamatan()!=null)?data.getKecamatan().trim().toUpperCase():"");
        innerResult.put("kab_kota", (data.getKabupatenKota()!=null)?data.getKabupatenKota().trim().toUpperCase():"");
        innerResult.put("nama_provinsi", (data.getProvinsi()!=null)?data.getProvinsi().trim().toUpperCase():"");
        innerResult.put("pendidikan", (data.getPendidikan()!=null)?data.getPendidikan().trim().toUpperCase():"");
        innerResult.put("pekerjaan", (data.getPekerjaan()!=null)?data.getPekerjaan().trim().toUpperCase():"");
        innerResult.put("jumlah_pembayaran", kursIndonesia.format(setoranAwal.getNominalSetoran()));
        innerResult.put("jumlah_terbilang", bilangx(setoranAwal.getNominalSetoran().doubleValue()).toUpperCase() + " RUPIAH");
        innerResult.put("virtual_account", (data.getVirtualAccount()!=null)?data.getVirtualAccount().trim().toUpperCase():"");
        LocalDate tanggal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        innerResult.put("tanggal", tanggal.getDayOfMonth() + " " + month[tanggal.getMonthValue()] + " " + tanggal.getYear());

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