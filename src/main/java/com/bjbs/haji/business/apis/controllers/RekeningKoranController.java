package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.SettingTbl;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import com.bjbs.haji.business.repositories.haji.SettingTblRepository;
import com.netflix.discovery.converters.Auto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/rekening-koran")
public class RekeningKoranController {

    @Value("${url.core-bank-server-2}")
    private String urlCoreBankServer2;

    @GetMapping("/readAll")
    public Object readAll(@RequestParam("tglBank") String tglBank, @RequestParam("noRekening") String noRekening,
    @RequestHeader("token") String token) throws ParseException {

        Date date = new SimpleDateFormat("yyyy/MM/dd").parse(tglBank);
        Map<String, Object> finalResult = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();

        JSONObject body = new JSONObject();
        body.put("accNbr", noRekening);
        body.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
        body.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(date));

        String url = urlCoreBankServer2 + "api/trx/get-info-mutasi";
//        String urlAccountSaving = urlCoreBankServer2 + "api/account-saving/" + noRekening;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);
            HttpEntity<String> request = new HttpEntity<String>(body.toString(), headers);
            String response = restTemplate.postForObject(url, request, String.class);

            JSONObject responseMutasi = new JSONObject(response);
            if (responseMutasi.get("rc").equals("00")) {
                JSONArray data = new JSONArray(responseMutasi.get("data").toString());
                if (data.length() > 0) {

                    for (Object object : data) {
                        result.add(new JSONObject(object.toString()).toMap());
                    }

//                HttpEntity<String> requestAccountSaving = new HttpEntity<String>(headers);
//                ResponseEntity<String> resultAccountSaving = restTemplate.exchange(urlAccountSaving, HttpMethod.GET, requestAccountSaving, String.class);
//
//                System.out.println("account saving : " + resultAccountSaving.getBody());
//
//                JSONObject accountSaving = new JSONObject(resultAccountSaving.getBody());
//                JSONObject dataAccountSaving = new JSONObject(accountSaving.get("data").toString());

                    finalResult.put("items", result);
                    finalResult.put("jumlahTransaksi", result.size());
                    Map<String, Object> lastItem = result.get(result.size() -1);
                    finalResult.put("saldoAkhir", lastItem.get("SALDO").toString());
                } else {
                    return new Response("99", null, "Tidak Ada Data Transaksi");
                }
            } else {
                return response;
            }
            return new Response("00", finalResult, "Berhasil");
        } catch (HttpClientErrorException e) {
            return new Response("99", null, e.getResponseBodyAsString());
        }
    }
}
