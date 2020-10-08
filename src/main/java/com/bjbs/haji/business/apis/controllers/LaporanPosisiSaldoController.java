package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.models.RekeningHaji;
import com.bjbs.haji.business.repositories.haji.RekeningHajiRepository;
import com.netflix.discovery.converters.Auto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/laporan-posisi-saldo")
public class LaporanPosisiSaldoController {

    @Value("${url.core-bank-server-2}")
    private String urlCoreBankServer2;

    @Autowired
    RekeningHajiRepository rekeningHajiRepository;

    @GetMapping("/generate")
    public void generate(HttpServletResponse response, @RequestParam("tglData") String tglData, @RequestHeader("token") String token) throws IOException, ParseException {
        Date date = new SimpleDateFormat("yyyy/MM/dd").parse(tglData);
        OutputStream outputStream = response.getOutputStream();
        Writer outputStreamWriter = new OutputStreamWriter(outputStream);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=TRS_425_" + new SimpleDateFormat("yyyyMMdd").format(date) + ".txt");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        try {
            List<RekeningHaji> rekeningHajiList = rekeningHajiRepository.findAll();
            if (rekeningHajiList.size() > 0) {
                for (RekeningHaji rekeningHaji : rekeningHajiList) {
                    outputStreamWriter.write("425");
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(new SimpleDateFormat("yyyy-MM-dd").format(date));
                    outputStreamWriter.write("|");
                    outputStreamWriter.write("B");
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getTipeLaporan());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getJenisKelompokDana());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getJenisProdukDana());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getCurrency());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getNoRekening());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(new SimpleDateFormat("yyyy-MM-dd").format(rekeningHaji.getTanggalBuka()));
                    outputStreamWriter.write("|");
                    if (rekeningHaji.getTanggalJatuhTempo() != null) {
                        outputStreamWriter.write(new SimpleDateFormat("yyyy-MM-dd").format(rekeningHaji.getTanggalJatuhTempo()));
                    } else {
                        outputStreamWriter.write("");
                    }
                    outputStreamWriter.write("|");
                    if (rekeningHaji.getJangkaWaktuDeposito() != null) {
                        outputStreamWriter.write(rekeningHaji.getJangkaWaktuDeposito());
                    } else {
                        outputStreamWriter.write("");
                    }
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getNisbah());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getEkuivalenRate());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getEkuivalenRate());

                    String urlAccountSaving = urlCoreBankServer2 + "api/account-saving/" + rekeningHaji.getNoRekening();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(token);

                    HttpEntity<String> requestAccountSaving = new HttpEntity<String>(headers);
                    ResponseEntity<String> resultAccountSaving = restTemplate.exchange(urlAccountSaving, HttpMethod.GET, requestAccountSaving, String.class);
                    JSONObject accountSaving = new JSONObject(resultAccountSaving.getBody());
                    JSONObject dataAccountSaving = new JSONObject(accountSaving.get("data").toString());
                    outputStreamWriter.write("|");
                    BigDecimal saldoAkhir = new BigDecimal(dataAccountSaving.get("SALDO_AKHIR").toString()).multiply(new BigDecimal(-1));
                    outputStreamWriter.write(saldoAkhir.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getJenisAkad());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(new SimpleDateFormat("yyyy-MM-dd").format(rekeningHaji.getTanggalTutup()));
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getNominalNisbah());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(rekeningHaji.getNoBilyet());
                    outputStreamWriter.write("\n");
                }
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
