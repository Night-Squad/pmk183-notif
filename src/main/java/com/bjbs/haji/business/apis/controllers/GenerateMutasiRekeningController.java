package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.SetoranPelunasan;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/mutasi-rekening")
public class GenerateMutasiRekeningController {

    @Value("${url.core-bank-server-2}")
    private String urlCoreBankServer2;

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @GetMapping("/generate")
    public void generate(@RequestParam("tglBank") String tglBank, @RequestParam("noRekening") String noRekening,
                           HttpServletResponse response, @RequestHeader("token") String token) throws ParseException, IOException {
        Date date = new SimpleDateFormat("yyyy/MM/dd").parse(tglBank);
        OutputStream outputStream = response.getOutputStream();
        Writer outputStreamWriter = new OutputStreamWriter(outputStream);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=RK_425_" + noRekening + "_" + new SimpleDateFormat("yyyyMMdd").format(date) + ".txt");

        JSONObject body = new JSONObject();
        body.put("accNbr", noRekening);
        body.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
        body.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(date));

        String url = urlCoreBankServer2 + "api/trx/get-info-mutasi";
        String urlAccountSaving = urlCoreBankServer2 + "api/account-saving/" + noRekening;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);
            HttpEntity<String> request = new HttpEntity<String>(body.toString(), headers);
            String responseCore = restTemplate.postForObject(url, request, String.class);

//            HttpEntity<String> requestAccountSaving = new HttpEntity<String>(headers);
//            ResponseEntity<String> resultAccountSaving = restTemplate.exchange(urlAccountSaving, HttpMethod.GET, requestAccountSaving, String.class);
//            JSONObject accountSaving = new JSONObject(resultAccountSaving.getBody());
//            JSONObject dataAccountSaving = new JSONObject(accountSaving.get("data").toString());

            JSONObject responseMutasi = new JSONObject(responseCore);
            if (responseMutasi.get("rc").equals("00")) {
                JSONArray data = new JSONArray(responseMutasi.get("data").toString());
                if (data.length() > 0) {
                    JSONObject lastItem = new JSONObject(data.get(data.length() -1).toString());
                    JSONObject firstItem = new JSONObject(data.get(0).toString());
                    double saldoAwal = (Double.parseDouble(firstItem.get("SALDO").toString()) * -1) - Double.parseDouble(firstItem.get("TXAMT").toString());
                    double saldoAkhir = Double.parseDouble(lastItem.get("SALDO").toString());
                    outputStreamWriter.write("#MULAI#\n");
                    outputStreamWriter.write("KODE BANK       : 425\n");
                    outputStreamWriter.write("NO REKENING     : " + noRekening + "\n");
                    outputStreamWriter.write("NAMA REKENING   : KAS HAJI UMUM QQ JEMAAH\n");
                    outputStreamWriter.write("TGL BUKA        : 2018-10-01\n");
                    outputStreamWriter.write("TGL VALUTA      : " +  new SimpleDateFormat("yyyy-MM-dd").format(date) + "\n");
                    String stringSaldoAwal = new BigDecimal(saldoAwal).setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
                    outputStreamWriter.write("SALDO AWAL      : " + stringSaldoAwal + "\n");
                    String stringSaldoAkhir = new BigDecimal(saldoAkhir * -1).setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
                    outputStreamWriter.write("SALDO AKHIR     : " + stringSaldoAkhir + "\n");
                    outputStreamWriter.write("##MUTASI-MULAI##\n");
                    outputStreamWriter.write("TGL_TRANS|TGL_VALUTA|KODE_TRANS|KODE_MUTASI|SALDO|KODE_SALDO|REFERENSI|NO_VALIDASI|NO_PORSI|NO_BILYET|NO_REKENING|NAMA_JEMAAH|NO_SURAT|KETERANGAN\n");

                    BigDecimal totalDebit = BigDecimal.ZERO;
                    BigDecimal totalCredit = BigDecimal.ZERO;
                    for (Object object : data) {
                        JSONObject item = new JSONObject(object.toString());
                        StringBuilder row = new StringBuilder();
                        row.append(new SimpleDateFormat("yyyy-MM-dd").format(date));
                        row.append("|");
                        row.append(new SimpleDateFormat("yyyy-MM-dd").format(date));
                        row.append("|");
                        if (item.get("TXMSG").toString().substring(0,3).equals("SAR") || item.get("TXMSG").toString().substring(0,3).equals("SLR")) {
                            row.append(item.get("TXMSG").toString(), 0, 3);
                        } else if (item.get("TXMSG").toString().substring(0,4).equals("XSAR") || item.get("TXMSG").toString().substring(0,4).equals("XSLR")) {
                            row.append(item.get("TXMSG").toString(), 0, 4);
                        } else {
                            row.append("EDIT");
                        }
                        row.append("|");
                        if (item.get("DBCR").toString().equals("0")) {
                            row.append("DB");
                            totalDebit = totalDebit.add(new BigDecimal(item.get("TXAMT").toString()));
                        } else {
                            row.append("CR");
                            totalCredit = totalCredit.add(new BigDecimal(item.get("TXAMT").toString()));
                        }
                        row.append("|");
                        row.append(item.get("TXAMT").toString());
                        row.append("|");
                        double saldo = Double.parseDouble(item.get("SALDO").toString());
                        row.append(new BigDecimal(saldo * -1).setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString());
                        row.append("|");
                        row.append(item.get("DBCR").toString().equals("0") ? "DB" : "CR");
                        row.append("|");
                        row.append(item.get("TXID").toString());
                        row.append("|");
                        if (item.get("TXMSG").toString().substring(0,3).equals("SAR")  || item.get("TXMSG").toString().substring(0,4).equals("XSAR")) {
                            SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByTransactionId(item.get("TXID").toString());
                            if (setoranAwal != null) {
                                row.append(setoranAwal.getNoValidasi());
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append(setoranAwal.getNoRekening());
                                row.append("|");
                                row.append(setoranAwal.getNamaJemaah());
                                row.append("|");
                                row.append("|");
                                row.append("Setoran Awal Reguler\n");
                            } else {
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("Setoran Awal Reguler\n");
                            }

                        } else if (item.get("TXMSG").toString().substring(0,3).equals("SLR")  || item.get("TXMSG").toString().substring(0,4).equals("XSLR")) {
                            SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.getSetoranPelunasanByTransactionId(item.get("TXID").toString());
                            if (setoranPelunasan != null) {
                                row.append("|");
                                row.append(setoranPelunasan.getNoPorsi());
                                row.append("|");
                                row.append("|");
                                row.append(setoranPelunasan.getNoRekening());
                                row.append("|");
                                row.append(setoranPelunasan.getNamaJemaah());
                                row.append("|");
                                row.append("|");
                                row.append("Setoran Pelunasan Reguler\n");
                            } else {
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("|");
                                row.append("Setoran Pelunasan Reguler\n");
                            }
                        } else {
                            row.append("|");
                            row.append("|");
                            row.append("|");
                            row.append("|");
                            row.append("|");
                            row.append("|");
                            row.append("EDIT\n");
                        }
                        outputStreamWriter.write(row.toString());
                    }
                    outputStreamWriter.write("##MUTASI-AKHIR##\n");
                    outputStreamWriter.write("JML_DATA|TOTAL_DB|TOTAL_CR|TOTAL_SALDO\n");
                    outputStreamWriter.write(String.valueOf(data.length()));
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(totalDebit.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(totalCredit.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString());
                    outputStreamWriter.write("|");
                    outputStreamWriter.write(stringSaldoAkhir + "\n");
                    outputStreamWriter.write("#AKHIR#\n");
                }
            }
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }
}
