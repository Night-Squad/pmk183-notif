package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.CetakSetoranAwalHajiData;
import com.bjbs.haji.business.apis.dtos.CetakSetoranAwalHajiResponse;
import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/get-data")
public class GetDataSetoranAwalController {

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @GetMapping("/cetak-setoran-awal")
    public Object getDataSetoranAwal(@RequestParam("noValidasi") String noValidasi) {
        try {
            SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoValidasi(noValidasi);

            String branchCode;
            if (setoranAwal.getChannel().getChannelId() == 4) {
                branchCode = "4251";
            } else {
                branchCode = setoranAwal.getNoRekening().substring(0,3);
            }

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

            Map<String, Object> result = new HashMap<>();
            result.put("setoranAwal", setoranAwal);
            result.put("data", data);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
