package com.bjbs.haji.business.repositories.controllers;

import com.bjbs.haji.business.apis.dtos.*;
import com.bjbs.haji.business.models.*;
import com.bjbs.haji.business.repositories.haji.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

@RestController
@RequestMapping("/api/setoran-pelunasan")
public class SetoranPelunasanCustomController {
    
    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Autowired
    TipeHajiRepository tipeHajiRepository;

    @Autowired
    MataUangRepository mataUangRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    PelunasanReversalHistoryRepository pelunasanReversalHistoryRepository;

    @Autowired
    StatusTransaksiRepository statusTransaksiRepository;

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    @Value("${url.core-bank-server}")
    private String urlCoreBankServer;

    @Value("${url.core-bank-server-2}")
    private String urlCoreBankServer2;

    @Value("${url.core-bank-client}")
    private String urlCoreBankClient;

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/inquiry")
    public Object inquiry(@RequestBody InquiryPelunasanDTO inquiryPelunasanDTO, @RequestParam("userCode") String userCode,
                          @RequestParam("userBranchCode") String userBranchCode, @RequestParam("branchCode") String branchCode) {
        try {
            InquiryPelunasanHajiData inquiryPelunasanHajiData = new InquiryPelunasanHajiData();
            inquiryPelunasanHajiData.setNomorPorsi(inquiryPelunasanDTO.getNoPorsi());
            Channel newChannel = channelRepository.findById(inquiryPelunasanDTO.getChannel().getChannelId()).orElse(null);
            inquiryPelunasanHajiData.setMerchantType(newChannel.getKodeMerchant());
            inquiryPelunasanHajiData.setNoRekening(inquiryPelunasanDTO.getNoRekening());
            inquiryPelunasanHajiData.setSettlementDate(new Date());
            inquiryPelunasanHajiData.setTerminalId(userBranchCode);
            inquiryPelunasanHajiData.setBranchCode(branchCode);

            String url = urlSwitchingApp + "api/switching_haji/inquiry_pelunasan";
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<InquiryPelunasanHajiData> request = new HttpEntity<InquiryPelunasanHajiData>(inquiryPelunasanHajiData, headers);
            String response = restTemplate.postForObject(url, request, String.class);
            JSONObject objectResponse = new JSONObject(response);
            System.out.println("result from switching : " + objectResponse.toString());

            if (objectResponse.getString("rc").equals("00")) {
                InquiryPelunasanHajiResponse result = mapper.readValue(objectResponse.get("data").toString(), InquiryPelunasanHajiResponse.class);
                return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), result, objectResponse.getString("message")));
            } else {
                return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), null, objectResponse.getString("message")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/create")
    public Object create(@RequestBody SetoranPelunasanDTO setoranPelunasanDTO, @RequestParam("userCode") String userCode,
                         @RequestParam("userBranchCode") String userBranchCode, @RequestParam("branchCode") String branchCode) {
        SetoranPelunasan setoranPelunasan = new SetoranPelunasan();
        try {
            Channel channel = new Channel(setoranPelunasanDTO.getChannel().getChannelId());
            setoranPelunasan.setChannel(channel);

            StatusTransaksi statusTransaksi = new StatusTransaksi(setoranPelunasanDTO.getStatusTransaksi().getStatusTransaksiId());
            setoranPelunasan.setStatusTransaksi(statusTransaksi);

            setoranPelunasan.setNoRekening(setoranPelunasanDTO.getNoRekening());
            setoranPelunasan.setNominalSetoran(setoranPelunasanDTO.getNominalSetoran());
            setoranPelunasan.setNoPorsi(setoranPelunasanDTO.getNoPorsi());
            setoranPelunasan.setBranchCode(setoranPelunasanDTO.getBranchCode());
            setoranPelunasan.setNamaJemaah(setoranPelunasanDTO.getNamaNasabah());
            setoranPelunasan.setEmbarkasi(setoranPelunasanDTO.getEmbarkasi());
            setoranPelunasan.setNilaiSetoranAwal(setoranPelunasanDTO.getNilaiSetoranAwal());
            setoranPelunasan.setBiayaBpih(setoranPelunasanDTO.getBiayaBpih());
            setoranPelunasan.setKurs(setoranPelunasanDTO.getKurs());
            setoranPelunasan.setBpihDalamRupiah(setoranPelunasanDTO.getBpihDalamRupiah());
            setoranPelunasan.setSisaPelunasan(setoranPelunasanDTO.getSisaPelunasan());
            setoranPelunasan.setFlagLunasTunda(Short.parseShort(setoranPelunasanDTO.getFlagLunasTunda()));
            setoranPelunasan.setTahunTunda(setoranPelunasanDTO.getTahunTunda());
            setoranPelunasan.setTipeHaji(new TipeHaji(setoranPelunasanDTO.getTipeHaji().getTipeHajiId()));
            setoranPelunasan.setMataUang(new MataUang(setoranPelunasanDTO.getMataUang().getMataUangId()));
            setoranPelunasan.setNoPihk(setoranPelunasanDTO.getNoPihk());
            setoranPelunasan.setNamaPihk(setoranPelunasanDTO.getNamaPihk());
            setoranPelunasan.setTerminalId(userBranchCode);
            setoranPelunasan.setNamaNasabah(setoranPelunasanDTO.getNamaJemaah());
            setoranPelunasan.setCreatedBy(userCode);
            setoranPelunasan.setCreatedDate(new Date());
            setoranPelunasan.setUpdatedBy(userCode);
            setoranPelunasan.setUpdatedDate(new Date());

            SetoranPelunasan result = setoranPelunasanRepository.save(setoranPelunasan);
            return ResponseEntity.ok().body(new Response("00", result, "Berhasil"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/approve")
    public Object approve(@RequestBody String request, @RequestParam("setoranPelunasanId") long setoranPelunasanId,
                          @RequestParam("userCode") String userCode, @RequestHeader("token") String token) {
        try {
            SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.findById(setoranPelunasanId).orElse(null);
            if (setoranPelunasan != null) {
                String url = urlCoreBankServer2 + "/api/developer/core/trx-v2";
                Date settlementDate = new Date();

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();

                try {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(token);
                    HttpEntity<String> requestBody = new HttpEntity<String>(request, headers);
                    String response = restTemplate.postForObject(url, requestBody, String.class);

                    System.out.println("Core Banking transaction response : " + response);
                    JSONObject journalResponse = new JSONObject(response);

                    if (journalResponse.getString("rc").equals("00")) {
                        JSONObject journalResult = new JSONObject(journalResponse.get("data").toString());
                        setoranPelunasan.setTransactionId(journalResult.get("TXID").toString());
                        setoranPelunasan.setStatusTransaksi(new StatusTransaksi(2));
                        setoranPelunasan.setTanggalTransaksi(settlementDate);
                        setoranPelunasan.setUpdatedDate(new Date());
                        setoranPelunasan.setUpdatedBy(userCode);
                        SetoranPelunasan result = setoranPelunasanRepository.save(setoranPelunasan);
                        return ResponseEntity.ok().body(new Response("00", result, "Setoran Pelunasan telah disetujui"));
                    } else {
                        return ResponseEntity.ok().body(new Response(journalResponse.getString("rc"), null, journalResponse.getString("message")));
                    }
                } catch (HttpClientErrorException e) {
                    JSONObject failedResponse = new JSONObject(e.getResponseBodyAsString());
                    return ResponseEntity.ok().body(new Response(failedResponse.getString("rc"), null, failedResponse.getString("message")));
                }
            }else{
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Pelunasan tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @GetMapping("/reject")
    public Object reject(@RequestParam("setoranPelunasanId") long setoranPelunasanId, @RequestParam("userCode") String userCode) {
        try {
            SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.findById(setoranPelunasanId).orElse(null);
            if(setoranPelunasan != null){
                setoranPelunasan.setStatusTransaksi(new StatusTransaksi(4));
                setoranPelunasan.setUpdatedBy(userCode);
                setoranPelunasan.setUpdatedDate(new Date());
                SetoranPelunasan result = setoranPelunasanRepository.save(setoranPelunasan);
                return ResponseEntity.ok().body(new Response("00", result, "Setoran Pelunasan telah ditolak"));
            } else {
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Pelunasan tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/pengajuan-reversal")
    public Object pengajuan(@RequestParam("setoranAwalId") long setoranPelunasanId, @RequestParam("userCode") String userCode) {
        try {
            SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.findById(setoranPelunasanId).orElse(null);
            if(setoranPelunasan != null) {
                setoranPelunasan.setStatusTransaksi(new StatusTransaksi(6));
                setoranPelunasan.setUpdatedBy(userCode);
                setoranPelunasan.setUpdatedDate(new Date());
                SetoranPelunasan result = setoranPelunasanRepository.save(setoranPelunasan);

                PelunasanReversalHistory pelunasanReversalHistory = new PelunasanReversalHistory();
                pelunasanReversalHistory.setSetoranPelunasanId(setoranPelunasan.getSetoranPelunasanId());
                pelunasanReversalHistory.setNoRekening(setoranPelunasan.getNoRekening());
                pelunasanReversalHistory.setNamaJemaah(setoranPelunasan.getNamaJemaah());
                pelunasanReversalHistory.setNominalSetoran(setoranPelunasan.getNominalSetoran());
                pelunasanReversalHistory.setNoArsip(setoranPelunasan.getTransactionId());
                pelunasanReversalHistory.setTanggalReversal(new Date());
                pelunasanReversalHistory.setBranchCode(setoranPelunasan.getBranchCode());
                pelunasanReversalHistory.setCreatedBy(userCode);
                pelunasanReversalHistory.setCreatedDate(new Date());
                pelunasanReversalHistory.setUpdatedBy(userCode);
                pelunasanReversalHistory.setUpdatedDate(new Date());
                pelunasanReversalHistory.setStatusActive(true);
                PelunasanReversalHistory resultReversal = pelunasanReversalHistoryRepository.save(pelunasanReversalHistory);
                return ResponseEntity.ok().body(new Response("00", resultReversal, "Pengajuan reversal Setoran Pelunasan berhasil"));
            } else {
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Pelunasan tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/reversal")
    public Object reversal(@RequestBody String request, @RequestParam("pelunasanReversalHistoryId") long pelunasanReversalHistoryId,
                           @RequestParam("userCode") String userCode, @RequestHeader("token") String token) {
        try {
            PelunasanReversalHistory pelunasanReversalHistory = pelunasanReversalHistoryRepository.findById(pelunasanReversalHistoryId).orElse(null);
            if (pelunasanReversalHistory != null) {
                SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.findById(pelunasanReversalHistory.getSetoranPelunasanId()).orElse(null);
                if(setoranPelunasan != null) {
                    String journalUrl = urlCoreBankServer2 + "api/trx-reversal";

                    JSONObject requestJson = new JSONObject(request);
                    requestJson.put("txId", setoranPelunasan.getTransactionId());
                    JSONArray param = new JSONArray(requestJson.get("param").toString());
                    JSONObject paramDebit = param.getJSONObject(0);
                    paramDebit.put("txId", setoranPelunasan.getTransactionId());
                    param.put(0, paramDebit);
                    JSONObject paramKredit = param.getJSONObject(1);
                    paramKredit.put("txId", setoranPelunasan.getTransactionId());
                    param.put(1, paramKredit);
                    requestJson.put("param", param);
                    System.out.println("new Request : " + requestJson.toString());

                    RestTemplate journalRestTemplate = new RestTemplate();
                    HttpHeaders journalHeaders = new HttpHeaders();
                    try {
                        journalHeaders.setContentType(MediaType.APPLICATION_JSON);
                        journalHeaders.setBearerAuth(token);
                        HttpEntity<String> requestBody = new HttpEntity<String>(requestJson.toString(), journalHeaders);
                        String cbResponse = journalRestTemplate.postForObject(journalUrl, requestBody, String.class);

                        System.out.println("Core Banking transaction response : " + cbResponse);
                        JSONObject journalResponse = new JSONObject(cbResponse);

                        if (journalResponse.getString("rc").equals("00")) {
                            setoranPelunasanRepository.delete(setoranPelunasan);

                            pelunasanReversalHistory.setStatusActive(false);
                            pelunasanReversalHistory.setUpdatedBy(userCode);
                            pelunasanReversalHistory.setUpdatedDate(new Date());
                            PelunasanReversalHistory result = pelunasanReversalHistoryRepository.save(pelunasanReversalHistory);
                            return ResponseEntity.ok().body(new Response("00", result, "Setoran Pelunasan telah direversal"));
                        } else {
                            return ResponseEntity.ok().body(new Response(journalResponse.getString("rc"), null, journalResponse.getString("message")));
                        }
                    } catch (HttpClientErrorException e) {
                        JSONObject failedResponse = new JSONObject(e.getResponseBodyAsString());
                        return ResponseEntity.ok().body(new Response(failedResponse.getString("rc"), null, failedResponse.getString("message")));
                    }
                } else {
                    return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Pelunasan tidak ditemukan"));
                }
            } else {
                return ResponseEntity.ok().body(new Response("99", null, "Data Pengajuan Reversal tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/pembayaran")
    public Object pembayaran(@RequestParam("setoranPelunasanId") long setoranPelunasanId, @RequestParam("userCode") String userCode,
                             @RequestParam("branchCode") String branchCode, @RequestParam("userBranchCode") String userBranchCode) {
        try {
            SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.findById(setoranPelunasanId).orElse(null);
            if (setoranPelunasan != null) {
                PembayaranPelunasanHajiRequest pembayaranPelunasanHajiRequest = new PembayaranPelunasanHajiRequest();
                pembayaranPelunasanHajiRequest.setNamaJemaah(setoranPelunasan.getNamaJemaah());
                pembayaranPelunasanHajiRequest.setNomorPorsi(setoranPelunasan.getNoPorsi());
                pembayaranPelunasanHajiRequest.setEmbarkasi(setoranPelunasan.getEmbarkasi());
                pembayaranPelunasanHajiRequest.setNilaiSetoranAwal(setoranPelunasan.getNilaiSetoranAwal().toString());
                pembayaranPelunasanHajiRequest.setBiayaBpih(setoranPelunasan.getBiayaBpih().toString());
                pembayaranPelunasanHajiRequest.setKurs(setoranPelunasan.getKurs().toString());
                pembayaranPelunasanHajiRequest.setBpihDalamRupiah(setoranPelunasan.getBpihDalamRupiah().toString());
                pembayaranPelunasanHajiRequest.setSisaPelunasan(setoranPelunasan.getSisaPelunasan().toString());
                pembayaranPelunasanHajiRequest.setFlagLunasTunda(String.valueOf(setoranPelunasan.getFlagLunasTunda()));
                pembayaranPelunasanHajiRequest.setTahunTunda(setoranPelunasan.getTahunTunda());
                pembayaranPelunasanHajiRequest.setJenisHaji(setoranPelunasan.getTipeHaji().getKodeHaji());
                pembayaranPelunasanHajiRequest.setKodeMataUang(setoranPelunasan.getMataUang().getKodeMataUang());
                pembayaranPelunasanHajiRequest.setKodePihk(setoranPelunasan.getNoPihk().equals("-") ? "" : setoranPelunasan.getNoPihk());
                pembayaranPelunasanHajiRequest.setNamaPihk(setoranPelunasan.getNamaPihk().equals("-") ? "" : setoranPelunasan.getNamaPihk());

                PembayaranPelunasanHajiData pembayaranPelunasanHajiData = new PembayaranPelunasanHajiData();
                pembayaranPelunasanHajiData.setNoRekening(setoranPelunasan.getNoRekening());
                pembayaranPelunasanHajiData.setMerchantType(setoranPelunasan.getChannel().getKodeMerchant());
                pembayaranPelunasanHajiData.setSettlementDate(setoranPelunasan.getTanggalTransaksi());
                pembayaranPelunasanHajiData.setTerminalId(userBranchCode);
                pembayaranPelunasanHajiData.setTransactionAmount(setoranPelunasan.getNominalSetoran().toString() + "00");
                pembayaranPelunasanHajiData.setBranchCode(branchCode);
                pembayaranPelunasanHajiData.setPembayaranPelunasanHajiRequest(pembayaranPelunasanHajiRequest);

                String url = urlSwitchingApp + "api/switching_haji/pembayaran_pelunasan";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<PembayaranPelunasanHajiData> request = new HttpEntity<PembayaranPelunasanHajiData>(pembayaranPelunasanHajiData, headers);
                String response = restTemplate.postForObject(url, request, String.class);
                JSONObject objectResponse = new JSONObject(response);

                System.out.println("result from switching : " + objectResponse.toString());

                if (objectResponse.getString("rc").equals("00")) {
                    PembayaranPelunasanHajiResponse data = mapper.readValue(objectResponse.get("data").toString(), PembayaranPelunasanHajiResponse.class);

                    setoranPelunasan.setJenisKelamin(Short.parseShort(data.getJenisKelamin()));
                    setoranPelunasan.setNamaAyah(data.getNamaOrangTua());
                    setoranPelunasan.setTempatLahir(data.getTempatLahir());
                    setoranPelunasan.setTanggalLahir(new SimpleDateFormat("ddMMyyyy").parse(data.getTanggalLahir()));
                    setoranPelunasan.setAlamat(data.getAlamat());
                    setoranPelunasan.setKelurahan(data.getDesa());
                    setoranPelunasan.setKecamatan(data.getKecamatan());
                    setoranPelunasan.setKabupatenKota(data.getKabupatenKota());
                    setoranPelunasan.setProvinsi(data.getProvinsi());
                    setoranPelunasan.setKodePos(data.getKodePos());
                    setoranPelunasan.setNamaBank(data.getNamaBank());
                    setoranPelunasan.setNamaCabang(data.getNamaCabangBank());
                    setoranPelunasan.setAlamatCabang(data.getAlamatCabangBank());
                    setoranPelunasan.setUmurTahun(data.getUmurTahun());
                    setoranPelunasan.setUmurBulan(data.getUmurBulan());
                    setoranPelunasan.setTahunPelunasanMasehi(data.getTahunPelunasanMasehi());
                    setoranPelunasan.setTahunPelunasanHijriah(data.getTahunPelunasanHijriah());
                    setoranPelunasan.setChecksum(data.getChecksum());
                    setoranPelunasan.setSystemTraceNumber(data.getStan());
                    setoranPelunasan.setRetRefNumber(data.getRetrievalReferenceNumber());
                    setoranPelunasan.setUpdatedDate(new Date());
                    setoranPelunasan.setUpdatedBy(userCode);
                    setoranPelunasan.setStatusTransaksi(new StatusTransaksi(3));

                    SetoranPelunasan result = setoranPelunasanRepository.save(setoranPelunasan);
                    return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), result, objectResponse.getString("message")));
                } else{
                    return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), null, objectResponse.getString("message")));
                }
            } else{
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Pelunasan todak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        String result=formatter.toString();
        formatter.close();
        return result;
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

}