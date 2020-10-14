package com.bjbs.haji.business.repositories.controllers;

import com.bjbs.haji.business.apis.dtos.*;
import com.bjbs.haji.business.models.*;
import com.bjbs.haji.business.repositories.haji.MataUangRepository;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import com.bjbs.haji.business.repositories.haji.PembatalanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/repo/pembatalan")
public class PembatalanCustomController {

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @Autowired
    PembatalanRepository pembatalanRepository;

    @Autowired
    MataUangRepository mataUangRepository;

    @Value("${url.core-bank-server}")
    private String urlCoreBankServer;

    @Value("${url.core-bank-client}")
    private String urlCoreBankClient;

    static final Logger logger = LogManager.getLogger(PembatalanCustomController.class.getName());
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/inquiry")
    public Object inquiry(@RequestBody PembatalanDTO pembatalanDTO, @RequestParam("userCode") String userCode,
                          @RequestParam("userBranchCode") String userBranchCode) {
        try {
            Pembatalan pembatalan = new Pembatalan();
            pembatalan.setBranchCode(pembatalanDTO.getBranchCode());
            pembatalan.setNomorPorsi(pembatalanDTO.getNomorPorsi());
            pembatalan.setTipePembatalan(new TipePembatalan(pembatalanDTO.getTipePembatalan().getTipePembatalanId()));
            pembatalan.setAlasanPembatalan(pembatalanDTO.getAlasanPembatalan());
            pembatalan.setTipeHaji(new TipeHaji(pembatalanDTO.getTipeHaji().getTipeHajiId()));
            pembatalan.setCreatedDate(new Date());
            pembatalan.setCreatedBy(userCode);
            pembatalan.setUpdatedDate(new Date());
            pembatalan.setUpdatedBy(userCode);

            InquiryPembatalanHajiData inquiryPembatalanHajiData = new InquiryPembatalanHajiData();
            if (pembatalan.getTipePembatalan().getTipePembatalanCode().equals("A")) {
                SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoRekening(pembatalanDTO.getNoRekening());
                if (setoranAwal != null) {
                    inquiryPembatalanHajiData.setNoRekening(setoranAwal.getNoRekening());
                    inquiryPembatalanHajiData.setMerchantType(setoranAwal.getChannel().getKodeMerchant());
                    inquiryPembatalanHajiData.setTerminalId(userBranchCode);
                    inquiryPembatalanHajiData.setSettlementDate(new Date());
                    inquiryPembatalanHajiData.setBranchCode(pembatalanDTO.getBranchCode());
                    inquiryPembatalanHajiData.setJenisPembatalan(pembatalan.getTipePembatalan().getTipePembatalanCode());
                    inquiryPembatalanHajiData.setNomorPorsi(pembatalanDTO.getNomorPorsi());
                } else {
                    return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal tidak ditemukan"));
                }
            } else {
                SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.getSetoranPelunasanByNoRekening(pembatalanDTO.getNoRekening());
                if (setoranPelunasan != null) {
                    inquiryPembatalanHajiData.setNoRekening(setoranPelunasan.getNoRekening());
                    inquiryPembatalanHajiData.setMerchantType(setoranPelunasan.getChannel().getKodeMerchant());
                    inquiryPembatalanHajiData.setTerminalId(userBranchCode);
                    inquiryPembatalanHajiData.setSettlementDate(new Date());
                    inquiryPembatalanHajiData.setBranchCode(pembatalanDTO.getBranchCode());
                    inquiryPembatalanHajiData.setJenisPembatalan(pembatalan.getTipePembatalan().getTipePembatalanCode());
                    inquiryPembatalanHajiData.setNomorPorsi(pembatalanDTO.getNomorPorsi());
                } else {
                    return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal tidak ditemukan"));
                }
            }

            String url = urlSwitchingApp + "api/switching_haji/inquiry_transaksi_pembatalan";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<InquiryPembatalanHajiData> request = new HttpEntity<InquiryPembatalanHajiData>(inquiryPembatalanHajiData, headers);
            String response = restTemplate.postForObject(url, request, String.class);
            JSONObject objectResponse = new JSONObject(response);

            System.out.println("result from switching : " + objectResponse.toString());

            if (objectResponse.getString("RC").equals("00")) {
                InquiryPembatalanHajiResponse data = mapper.readValue(objectResponse.get("data").toString(), InquiryPembatalanHajiResponse.class);
                pembatalan.setNamaJemaah(data.getNamaJemaah());
                pembatalan.setMataUang(mataUangRepository.getMataUangByCode(data.getKodeMataUang()));
                pembatalan.setNilaiSetoranAwal(new BigInteger(data.getNilaiSetoranAwal()));
                pembatalan.setTanggalSetoranAwal(new SimpleDateFormat("ddMMyyyy").parse(data.getTanggalSetoranAwal()));
                pembatalan.setKodeBpsBpihAwal(data.getKodeBankSetoranAwal());
                pembatalan.setNilaiSetoranPelunasan(new BigInteger(data.getNilaiSetoranPelunasan()));
                pembatalan.setTanggalSetoranPelunasan(new SimpleDateFormat("ddMMyyyy").parse(data.getTanggalSetoranPelunasan()));
                pembatalan.setKodeBpsBpihPelunasan(data.getKodeBankSetoranPelunasan());
                Pembatalan result = pembatalanRepository.save(pembatalan);
                return ResponseEntity.ok().body(new Response(objectResponse.getString("RC"), result, objectResponse.getString("message")));
            } else {
                return ResponseEntity.ok().body(new Response(objectResponse.getString("RC"), null, objectResponse.getString("message")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/transaksi")
    public Object transaksi(@RequestBody String request, @RequestParam("pembatalanId") long pembatalanId,
                            @RequestParam("userCode") String userCode,
                            @RequestParam("userBranchCode") String userBranchCode) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Date settlementDate = new Date();
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Pembatalan pembatalan = pembatalanRepository.findById(pembatalanId).orElse(null);
            if (pembatalan != null) {
                PembayaranPembatalanHajiRequest pembayaranPembatalanHajiRequest = new PembayaranPembatalanHajiRequest();
                pembayaranPembatalanHajiRequest.setNomorPorsi(pembatalan.getNomorPorsi());
                pembayaranPembatalanHajiRequest.setJenisPembatalan(pembatalan.getTipePembatalan().getTipePembatalanCode());
                pembayaranPembatalanHajiRequest.setAlasanPembatalan(pembatalan.getAlasanPembatalan());
                pembayaranPembatalanHajiRequest.setNamaJemaah(pembatalan.getNamaJemaah());
                pembayaranPembatalanHajiRequest.setKodeMataUang(pembatalan.getMataUang().getKodeMataUang());
                pembayaranPembatalanHajiRequest.setNilaiSetoranAwal(pembatalan.getNilaiSetoranAwal().toString());
                pembayaranPembatalanHajiRequest.setTanggalSetoranAwal(new SimpleDateFormat("ddMMyyyy").format(pembatalan.getTanggalSetoranAwal()));
                pembayaranPembatalanHajiRequest.setKodeBankSetoranAwal(pembatalan.getKodeBpsBpihAwal());
                pembayaranPembatalanHajiRequest.setNilaiSetoranPelunasan(pembatalan.getNilaiSetoranPelunasan().toString());
                pembayaranPembatalanHajiRequest.setKodeBankSetoranPelunasan(pembatalan.getKodeBpsBpihPelunasan());
                pembayaranPembatalanHajiRequest.setNominalPembatalan(pembatalan.getNominalPembatalan().toString());
                pembayaranPembatalanHajiRequest.setNominalPembatalanBiayaOperasional(pembatalan.getNominalPembatalanBiayaOperasional().toString());
                pembayaranPembatalanHajiRequest.setJenisHaji(pembatalan.getTipeHaji().getKodeHaji());

                PembayaranPembatalanHajiData pembayaranPembatalanHajiData = new PembayaranPembatalanHajiData();
                pembayaranPembatalanHajiData.setNoRekening(pembatalan.getNoRekening());
                pembayaranPembatalanHajiData.setMerchantType("");
                pembayaranPembatalanHajiData.setSettlementDate(settlementDate);
                pembayaranPembatalanHajiData.setTerminalId(userBranchCode);
                pembayaranPembatalanHajiData.setBranchCode(pembatalan.getBranchCode());
                pembayaranPembatalanHajiData.setPembayaranPembatalanHajiRequest(pembayaranPembatalanHajiRequest);

                String url = urlSwitchingApp + "api/switching_haji/pembayaran_pembatalan";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<PembayaranPembatalanHajiData> requestIso = new HttpEntity<PembayaranPembatalanHajiData>(pembayaranPembatalanHajiData, headers);
                String response = restTemplate.postForObject(url, requestIso, String.class);
                JSONObject objectResponse = new JSONObject(response);

                System.out.println("result from switching : " + objectResponse.toString());

                if (objectResponse.getString("RC").equals("00")) {
                    JSONObject resultIso = new JSONObject(objectResponse.getString("data"));
                    pembatalan.setNomorSuratPembatalan(resultIso.getString("nomorSuratPembatalan"));

                    String journalUrl = urlCoreBankServer + "Gateway/service/v2/postData";
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmmss");
                    SimpleDateFormat dateGenerateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeGenerateFormatter = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat dateRkFormatter = new SimpleDateFormat("dd-MM-yyyy");
                    String ipCoreBank = urlCoreBankClient;
                    String data = "00005" + ipCoreBank + dateGenerateFormatter.format(settlementDate) + timeGenerateFormatter.format(settlementDate);

                    String authKey = calculateRFC2104HMAC(data, "fosgw");
                    JSONObject requestJson = new JSONObject(request);
                    requestJson.put("authKey", authKey);
                    requestJson.put("txDate", dateFormatter.format(settlementDate));
                    requestJson.put("txHour", timeFormatter.format(settlementDate));
                    requestJson.put("date", dateRkFormatter.format(settlementDate));
                    requestJson.put("date_rk", dateRkFormatter.format(settlementDate));

                    logger.debug("data : " + data);
                    logger.debug("authKey : " + authKey);
                    logger.debug("dateFormatter : " + dateFormatter.format(settlementDate));
                    logger.debug("timeFormatter : " + timeFormatter.format(settlementDate));
                    logger.debug("new Request : " + requestJson.toString());

                    RestTemplate journalRestTemplate = new RestTemplate();
                    HttpHeaders journalHeaders = new HttpHeaders();

                    try {
                        journalHeaders.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<String> requestBody = new HttpEntity<String>(requestJson.toString());
                        String cbResponse = journalRestTemplate.postForObject(journalUrl, requestBody, String.class);

                        logger.debug("Core Banking transaction response : " + cbResponse);
                        JSONObject journalResponse = new JSONObject(cbResponse);

                        if (journalResponse.getString("rCode").equals("00")) {
                            pembatalan.setTanggalPembatalan(settlementDate);
                            pembatalan.setUpdatedDate(settlementDate);
                            pembatalan.setUpdatedBy(userCode);

                            if (pembatalan.getTipePembatalan().getTipePembatalanCode().equals("A")) {
                                SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoRekening(pembatalan.getNoRekening());
                                setoranAwal.setStatusTransaksi(new StatusTransaksi(3));
                                setoranAwal.setUpdatedDate(settlementDate);
                                setoranAwal.setUpdatedBy(userCode);
                                setoranAwalRepository.save(setoranAwal);
                            } else {
                                SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.getSetoranPelunasanByNoRekening(pembatalan.getNoRekening());
                                setoranPelunasan.setStatusTransaksi(new StatusTransaksi(3));
                                setoranPelunasan.setUpdatedDate(settlementDate);
                                setoranPelunasan.setUpdatedBy(userCode);
                                setoranPelunasanRepository.save(setoranPelunasan);
                            }

                            Pembatalan resultPembatalan = pembatalanRepository.save(pembatalan);
                            return ResponseEntity.ok().body(new Response("00", resultPembatalan, "Transaksi Pembatalan " + pembatalan.getTipePembatalan().getTipePembatalanName() + " berhasil"));
                        } else {
                            return ResponseEntity.ok().body(new Response(journalResponse.getString("rCode"), null, journalResponse.getString("message")));
                        }
                    } catch (HttpClientErrorException e) {
                        JSONObject failedResponse = new JSONObject(e.getResponseBodyAsString());
                        return ResponseEntity.ok().body(new Response(failedResponse.getString("rCode"), null, failedResponse.getString("message")));
                    }
                } else {
                    return ResponseEntity.ok().body(new Response(objectResponse.getString("RC"), null, objectResponse.getString("message")));
                }
            } else {
                return ResponseEntity.ok().body(new Response("99", null, "Data setoran tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
