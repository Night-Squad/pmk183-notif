package com.bjbs.haji.business.repositories.controllers;

import com.bjbs.haji.business.apis.controllers.CetakResiSetoranAwal2Controller;
import com.bjbs.haji.business.apis.controllers.CetakResiSetoranAwalController;
import com.bjbs.haji.business.apis.controllers.utility.CustomMultipartFile;
import com.bjbs.haji.business.apis.dtos.*;
import com.bjbs.haji.business.models.*;
import com.bjbs.haji.business.repositories.haji.AwalReversalHistoryRepository;
import com.bjbs.haji.business.repositories.haji.CitiesRepository;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.service.SetoranAwalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/repo/setoran-awal")
public class SetoranAwalCustomController {
    
    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    AwalReversalHistoryRepository awalReversalHistoryRepository;

    @Autowired
    CitiesRepository citiesRepository;

    @Autowired
    private SetoranAwalService setoranAwalService;

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    @Value("${url.core-bank-server}")
    private String urlCoreBankServer;

    @Value("${url.core-bank-server-2}")
    private String urlCoreBankServer2;

    @Value("${url.core-bank-client}")
    private String urlCoreBankClient;

    @Value("${url.app}")
    private String siskohatUrl;

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    CetakResiSetoranAwal2Controller cetakResiSetoranAwalController;

    @PostMapping("/inquiry")
    public Object create(@RequestBody SetoranAwalDTO setoranAwalDTO, @RequestParam("userCode") String userCode, @RequestParam("userBranchCode") String userBranchCode) {

//        if (!setoranAwalDTO.getNoRekening().substring(5,7).equals("07")) {
//            return ResponseEntity.ok().body(new Response("98", null, "Nomor Rekening bukan tabungan Haji!"));
//        }

        // validation at 15.00
        LocalTime currentTime = LocalTime.now();
        boolean hitValidation = setoranAwalService.ValidationTrxTime(currentTime);
        if(!hitValidation) {
            return ResponseEntity.badRequest().body(new Response("99", null, "Setoran hanya bisa dilakukan pada jam 08.00 s.d. 15.00"));
        }

        SetoranAwal setoranAwal = new SetoranAwal();
        try {
            Channel channel = new Channel(setoranAwalDTO.getChannel().getChannelId());
            setoranAwal.setChannel(channel);

            TipeHaji tipeHaji = new TipeHaji(setoranAwalDTO.getTipeHaji().getTipeHajiId());
            setoranAwal.setTipeHaji(tipeHaji);

            MataUang mataUang = new MataUang(setoranAwalDTO.getMataUang().getMataUangId());
            setoranAwal.setMataUang(mataUang);

            Pendidikan pendidikan = new Pendidikan(setoranAwalDTO.getPendidikan().getPendidikanId());
            setoranAwal.setPendidikan(pendidikan);

            Pekerjaan pekerjaan = new Pekerjaan(setoranAwalDTO.getPekerjaan().getPekerjaanId());
            setoranAwal.setPekerjaan(pekerjaan);

            StatusTransaksi statusTransaksi = new StatusTransaksi(setoranAwalDTO.getStatusTransaksi().getStatusTransaksiId());
            setoranAwal.setStatusTransaksi(statusTransaksi);

            StatusKawin statusKawin = new StatusKawin(setoranAwalDTO.getStatusKawin().getStatusKawinId());
            setoranAwal.setStatusKawin(statusKawin);

            setoranAwal.setNamaJemaah(setoranAwalDTO.getNamaJemaah());
            setoranAwal.setNoIdentitas(setoranAwalDTO.getNoIdentitas());
            setoranAwal.setNoRekening(setoranAwalDTO.getNoRekening());
            setoranAwal.setTanggalLahir(setoranAwalDTO.getTanggalLahir());
            setoranAwal.setTempatLahir(setoranAwalDTO.getTempatLahir());
            setoranAwal.setJenisKelamin(setoranAwalDTO.getJenisKelamin());
            setoranAwal.setAlamat(setoranAwalDTO.getAlamat());
            setoranAwal.setKodePos(setoranAwalDTO.getKodePos());
            setoranAwal.setKelurahan(setoranAwalDTO.getKelurahan());
            setoranAwal.setKecamatan(setoranAwalDTO.getKecamatan());
            Cities city = citiesRepository.findByCityCodeCbs(setoranAwalDTO.getKabupatenKotaId());
            setoranAwal.setKabupatenKota(city.getCityName());
            setoranAwal.setKabupatenKotaId(setoranAwalDTO.getKabupatenKotaId());
            setoranAwal.setNominalSetoran(setoranAwalDTO.getNominalSetoran());
            setoranAwal.setTerminalId(userBranchCode);

            setoranAwal.setBranchCode(setoranAwalDTO.getBranchCode());
            setoranAwal.setNamaAyah(setoranAwalDTO.getNamaAyah());
            setoranAwal.setCreatedBy(userCode);
            setoranAwal.setCreatedDate(new Date());
            setoranAwal.setUpdatedBy(userCode);
            setoranAwal.setUpdatedDate(new Date());

            SetoranAwal result = setoranAwalRepository.save(setoranAwal);
            if (result != null) {
                return ResponseEntity.ok().body(new Response("00", result, "Inquiry Setoran Awal berhasil"));
            } else {
                return ResponseEntity.ok().body(new Response("99", result, "Inquiry Setoran Awal gagal"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/approve")
    public Object approve(@RequestParam("setoranAwalId") long setoranAwalId, @RequestParam("userCode") String userCode,
                          @RequestBody String request, @RequestHeader("token") String token) {

        try {
            SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranAwalId).orElse(null);
            if(setoranAwal != null){
                String url = urlCoreBankServer2 + "/api/developer/core/trx-v2";
                Date settlementDate = new Date();

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();

                try {
                    System.out.println("------------------------- REQUEST BODY JOURNAL ------------------------");
                    System.out.println(request);
                    System.out.println("-----------------------------------------------------------------------");

                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(token);
                    HttpEntity<String> requestBody = new HttpEntity<String>(request, headers);
                    String response = restTemplate.postForObject(url, requestBody, String.class);

                    System.out.println("--------------------------- RESPONSE JOURNAL --------------------------");
                    System.out.println(response);
                    System.out.println("-----------------------------------------------------------------------");
                    JSONObject journalResponse = new JSONObject(response);

                    if (journalResponse.getString("rc").equals("00")) {
                        JSONObject journalResult = new JSONObject(journalResponse.get("data").toString());
                        setoranAwal.setTransactionId(journalResult.get("TXID").toString());
                        setoranAwal.setStatusTransaksi(new StatusTransaksi(2));
                        setoranAwal.setTanggalTransaksi(settlementDate);
                        setoranAwal.setUpdatedDate(new Date());
                        setoranAwal.setUpdatedBy(userCode);
                        SetoranAwal result = setoranAwalRepository.save(setoranAwal);
                        return ResponseEntity.ok().body(new Response("00", result, "Setoran Awal telah disetujui"));
                    } else {
                        return ResponseEntity.ok().body(new Response(journalResponse.getString("rc"), null, journalResponse.getString("message")));
                    }
                } catch (HttpClientErrorException e) {
                    JSONObject failedResponse = new JSONObject(e.getResponseBodyAsString());
                    return ResponseEntity.ok().body(new Response(failedResponse.getString("rc"), null, failedResponse.getString("message")));
                }
            }else{
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @GetMapping("/reject")
    public Object reject(@RequestParam("setoranAwalId") long setoranAwalId, @RequestParam("userCode") String userCode) {
        try {
            SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranAwalId).orElse(null);
            if(setoranAwal != null){
                setoranAwal.setStatusTransaksi(new StatusTransaksi(4));
                setoranAwal.setUpdatedBy(userCode);
                setoranAwal.setUpdatedDate(new Date());
                SetoranAwal result = setoranAwalRepository.save(setoranAwal);
                return ResponseEntity.ok().body(new Response("00", result, "Setoran Awal telah ditolak"));
            } else {
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/pengajuan-reversal")
    public Object pengajuan(@RequestParam("setoranAwalId") long setoranAwalId, @RequestParam("userCode") String userCode) {
        try {
            SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranAwalId).orElse(null);
            if(setoranAwal != null) {
                setoranAwal.setStatusTransaksi(new StatusTransaksi(6));
                setoranAwal.setUpdatedBy(userCode);
                setoranAwal.setUpdatedDate(new Date());
                SetoranAwal result = setoranAwalRepository.save(setoranAwal);

                AwalReversalHistory awalReversalHistory = new AwalReversalHistory();
                awalReversalHistory.setSetoranAwalId(setoranAwal.getSetoranAwalId());
                awalReversalHistory.setNoRekening(setoranAwal.getNoRekening());
                awalReversalHistory.setNamaJemaah(setoranAwal.getNamaJemaah());
                awalReversalHistory.setNominalSetoran(setoranAwal.getNominalSetoran());
                awalReversalHistory.setNoArsip(setoranAwal.getTransactionId());
                awalReversalHistory.setTanggalReversal(new Date());
                awalReversalHistory.setBranchCode(setoranAwal.getBranchCode());
                awalReversalHistory.setCreatedBy(userCode);
                awalReversalHistory.setCreatedDate(new Date());
                awalReversalHistory.setUpdatedBy(userCode);
                awalReversalHistory.setUpdatedDate(new Date());
                awalReversalHistory.setStatusActive(true);
                AwalReversalHistory resultReversal = awalReversalHistoryRepository.save(awalReversalHistory);
                return ResponseEntity.ok().body(new Response("00", resultReversal, "Pengajuan reversal Setoran Awal berhasil"));
            } else {
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/reversal")
    public Object reversal(@RequestBody String request, @RequestParam("awalReversalHistoryId") long awalReversalHistoryId,
                           @RequestParam("userCode") String userCode, @RequestHeader("token") String token) {
        try {
            AwalReversalHistory awalReversalHistory = awalReversalHistoryRepository.findById(awalReversalHistoryId).orElse(null);
            if (awalReversalHistory != null) {
                SetoranAwal setoranAwal = setoranAwalRepository.findById(awalReversalHistory.getSetoranAwalId()).orElse(null);
                if(setoranAwal != null) {
                    String journalUrl = urlCoreBankServer2 + "api/trx-reversal";

                    JSONObject requestJson = new JSONObject(request);
                    requestJson.put("txId", setoranAwal.getTransactionId());

                    JSONArray param = new JSONArray(requestJson.get("param").toString());
                    JSONObject paramDebit = param.getJSONObject(0);
                    paramDebit.put("txId", setoranAwal.getTransactionId());
                    param.put(0, paramDebit);
                    JSONObject paramKredit = param.getJSONObject(1);
                    paramKredit.put("txId", setoranAwal.getTransactionId());
                    param.put(1, paramKredit);
                    requestJson.put("param", param);
                    System.out.println("------------------------- REQUEST BODY REVERSAL ------------------------");
                    System.out.println(requestJson.toString());
                    System.out.println("------------------------------------------------------------------------");

                    RestTemplate journalRestTemplate = new RestTemplate();
                    HttpHeaders journalHeaders = new HttpHeaders();
                    try {
                        journalHeaders.setContentType(MediaType.APPLICATION_JSON);
                        journalHeaders.setBearerAuth(token);
                        HttpEntity<String> requestBody = new HttpEntity<String>(requestJson.toString(), journalHeaders);
                        String cbResponse = journalRestTemplate.postForObject(journalUrl, requestBody, String.class);

                        System.out.println("--------------------------- RESPONSE REVERSAL --------------------------");
                        System.out.println(cbResponse);
                        System.out.println("------------------------------------------------------------------------");
                        JSONObject journalResponse = new JSONObject(cbResponse);

                        if (journalResponse.getString("rc").equals("00")) {
                            setoranAwalRepository.delete(setoranAwal);
                            awalReversalHistory.setStatusActive(false);
                            awalReversalHistory.setUpdatedBy(userCode);
                            awalReversalHistory.setUpdatedDate(new Date());
                            AwalReversalHistory result = awalReversalHistoryRepository.save(awalReversalHistory);
                            return ResponseEntity.ok().body(new Response("00", result, "Setoran Awal telah direversal"));
                        } else {
                            return ResponseEntity.ok().body(new Response(journalResponse.getString("rc"), null, journalResponse.getString("message")));
                        }
                    } catch (HttpClientErrorException e) {
                        JSONObject failedResponse = new JSONObject(e.getResponseBodyAsString());
                        return ResponseEntity.ok().body(new Response(failedResponse.getString("rc"), null, failedResponse.getString("message")));
                    }
                } else {
                    return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal tidak ditemukan"));
                }
            }else {
                return ResponseEntity.ok().body(new Response("99", null, "Data Pengajuan Reversal tidak ditemukan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/pembayaran")
    public Object pembayaran(@RequestParam("setoranAwalId") long setoranAwalId, @RequestParam("userCode") String userCode,
                             @RequestParam("branchCode") String branchCode, @RequestParam("userBranchCode") String userBranchCode,
                             @RequestParam("cityId") String cityId, @RequestParam("provinceId") String provinceId,
                             @RequestHeader("token-kemenag") String tokenKemenag) {
        try {
            // validation at 15.00
            LocalTime currentTime = LocalTime.now();
            boolean hitValidation = setoranAwalService.ValidationTrxTime(currentTime);
            if(!hitValidation) {
                return ResponseEntity.badRequest().body(new Response("99", null, "Setoran hanya bisa dilakukan pada jam 08.00 s.d. 15.00"));
            }

            SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranAwalId).orElse(null);
            if (setoranAwal != null) {
                SetoranAwalHajiRequest setoranAwalHajiRequest = new SetoranAwalHajiRequest();
                setoranAwalHajiRequest.setJenisHaji(setoranAwal.getTipeHaji().getKodeHaji());
                setoranAwalHajiRequest.setJenisKelamin(String.valueOf(setoranAwal.getJenisKelamin()));
                setoranAwalHajiRequest.setKodePekerjaan(String.valueOf(setoranAwal.getPekerjaan().getKodePekerjaan()));
                setoranAwalHajiRequest.setKodePendidikan(String.valueOf(setoranAwal.getPendidikan().getKodePendidikan()));
                setoranAwalHajiRequest.setKodeStatusPernikahan(String.valueOf(setoranAwal.getStatusKawin().getStatusKawinKemenag()));
                setoranAwalHajiRequest.setNamaJemaah(setoranAwal.getNamaJemaah());
                setoranAwalHajiRequest.setNoIdentitas(setoranAwal.getNoIdentitas());
                setoranAwalHajiRequest.setTanggalLahir(new SimpleDateFormat("ddMMyyyy").format(setoranAwal.getTanggalLahir()));
                setoranAwalHajiRequest.setTempatLahir(setoranAwal.getTempatLahir());
                setoranAwalHajiRequest.setAlamat(setoranAwal.getAlamat());
                setoranAwalHajiRequest.setKodePos(setoranAwal.getKodePos());
                setoranAwalHajiRequest.setDesa(setoranAwal.getKelurahan());
                setoranAwalHajiRequest.setKecamatan(setoranAwal.getKecamatan());
                setoranAwalHajiRequest.setKabupatenKota(setoranAwal.getKabupatenKota());
                Cities city = citiesRepository.findByCityCodeCbs(setoranAwal.getKabupatenKotaId());
                setoranAwalHajiRequest.setKodeKabupatenKota(city.getCityCode());
                setoranAwalHajiRequest.setKodeProvinsi(city.getProvinces().getProvinceCode());
                setoranAwalHajiRequest.setNamaAyah(setoranAwal.getNamaAyah());

                SetoranAwalHajiData setoranAwalHajiData = new SetoranAwalHajiData();
                setoranAwalHajiData.setNoRekening(setoranAwal.getNoRekening());
                setoranAwalHajiData.setMerchantType(setoranAwal.getChannel().getKodeMerchant());
                setoranAwalHajiData.setSettlementDate(setoranAwal.getTanggalTransaksi());
                setoranAwalHajiData.setTerminalId(userBranchCode);
                setoranAwalHajiData.setTransactionAmount(setoranAwal.getNominalSetoran().toString() + "00");
                setoranAwalHajiData.setBranchCode(branchCode);
                setoranAwalHajiData.setSetoranAwalHajiRequest(setoranAwalHajiRequest);

                System.out.println("------------------ REQUEST BODY SWITCHING PEMBAYARAN SETORAN AWAL ------------------");
                System.out.println(mapper.writeValueAsString(setoranAwalHajiData));
                System.out.println("------------------------------------------------------------------------------------");

                String url = urlSwitchingApp + "api/switching_haji/pembayaran_setoran_awal";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<SetoranAwalHajiData> request = new HttpEntity<SetoranAwalHajiData>(setoranAwalHajiData, headers);
                String response = restTemplate.postForObject(url, request, String.class);
                JSONObject objectResponse = new JSONObject(response);

                System.out.println("--------------------- RESPONSE SWITCHING PEMBAYARAN SETORAN AWAL -------------------");
                System.out.println(objectResponse.toString());
                System.out.println("------------------------------------------------------------------------------------");

                if (objectResponse.getString("rc").equals("00")) {
                    SetoranAwalHajiResponse data = mapper.readValue(objectResponse.get("data").toString(), SetoranAwalHajiResponse.class);

                    setoranAwal.setNoValidasi(data.getNomorValidasi());
                    setoranAwal.setSystemTraceNumber(data.getStan());
                    setoranAwal.setRetRefNumber(data.getRetrievalReferenceNumber());
                    setoranAwal.setKloter(data.getKloter());
                    setoranAwal.setTahunBerangkat(data.getTahunBerangkat());
                    setoranAwal.setEmbarkasi(data.getEmbarkasi());
                    setoranAwal.setStatusTransaksi(new StatusTransaksi(3));
                    setoranAwal.setVirtualAccount(data.getVirtualAccount());
                    setoranAwal.setUpdatedDate(new Date());
                    setoranAwal.setUpdatedBy(userCode);
                    setoranAwalRepository.save(setoranAwal);
                    Map<String, Object> resultUpload = new HashMap<>();
                    try {
                        String uploadBuktiSetoranUrl = siskohatUrl + "siskohat/bank/setoranawal";

                        Map<String, String> parameter = new HashMap<>();
                        parameter.put("setoranAwalId", setoranAwal.getSetoranAwalId().toString());
                        Map<String, String> header = new HashMap<>();
                        SetoranAwalDTO usersDTO = new SetoranAwalDTO();
                        ResponseEntity<byte[]> responseFile = cetakResiSetoranAwalController.ionaGenerateAsPDF(usersDTO, parameter, header);

                        String filename = "bukti-setoran-awal-" + LocalDateTime.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() + ".pdf";
                        CustomMultipartFile multipartFile = new
                                CustomMultipartFile(responseFile.getBody(), "application/pdf", filename);
                        multipartFile.transferTo(multipartFile.getFile());

                        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
                        map.add("no_validasi", data.getNomorValidasi());
                        map.add("upload", new FileSystemResource(multipartFile.getFile()));

                        System.out.println("------------------------- REQUEST BODY BUKTI SETORAN AWAL ------------------");
                        System.out.println("No Validasi : " + map.get("no_validasi"));
                        System.out.println("File : " + multipartFile.getName());
                        System.out.println("token kemenag : " + tokenKemenag);
                        System.out.println("----------------------------------------------------------------------------");

                        RestTemplate restTemplateUpload = new RestTemplate();
                        HttpHeaders headersUpload = new HttpHeaders();
                        headersUpload.setContentType(MediaType.MULTIPART_FORM_DATA);
                        headersUpload.set("x-access-key", tokenKemenag);
                        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headersUpload);

                        ResponseEntity<String> responseUpload = restTemplateUpload.postForEntity(uploadBuktiSetoranUrl, requestEntity, String.class);

                        resultUpload.put("uploadBukti", responseUpload.getBody());
                        JSONObject jsonResultUpload = new JSONObject(responseUpload.getBody());
                        System.out.println("------------------------- RESPONSE UPLOAD BUKTI SETORAN AWAL ------------------");
                        System.out.println(jsonResultUpload.toString());
                        System.out.println("-------------------------------------------------------------------------------");
                        setoranAwal.setIsUploaded(jsonResultUpload.getString("RC").equals("00"));

                    } catch (HttpClientErrorException hcex) {
                        hcex.printStackTrace();
                        resultUpload.put("uploadBukti", hcex.getResponseBodyAsString());
                        setoranAwal.setIsUploaded(false);
                    } catch (Exception ioe) {
                        ioe.printStackTrace();
                        Map<String, String> error = new HashMap<>();
                        error.put("RC", "99");
                        error.put("message", ioe.getLocalizedMessage());
                        resultUpload.put("uploadBukti", error);
                        setoranAwal.setIsUploaded(false);
                    }

                    setoranAwalRepository.save(setoranAwal);

                    return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), setoranAwal, objectResponse.getString("message")));
                } else{
                    return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), null, objectResponse.getString("message")));
                }
            } else{
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal todak ditemukan"));
            }
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/pembayaran-multichannel")
    public Object pembayaranMultichannel(@RequestParam("setoranAwalId") long setoranAwalId, @RequestParam("userCode") String userCode,
                             @RequestParam("branchCode") String branchCode, @RequestParam("userBranchCode") String userBranchCode,
                             @RequestParam("cityId") String cityId, @RequestParam("provinceId") String provinceId) {
        try {

            // validation at 15.00
            LocalTime currentTime = LocalTime.now();
            boolean hitValidation = setoranAwalService.ValidationTrxTime(currentTime);
            if(!hitValidation) {
                return ResponseEntity.badRequest().body(new Response("99", null, "Setoran hanya bisa dilakukan pada jam 08.00 s.d. 15.00"));
            }

            SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranAwalId).orElse(null);
            if (setoranAwal != null) {
                SetoranAwalHajiRequest setoranAwalHajiRequest = new SetoranAwalHajiRequest();
                setoranAwalHajiRequest.setJenisHaji(setoranAwal.getTipeHaji().getKodeHaji());
                setoranAwalHajiRequest.setJenisKelamin(String.valueOf(setoranAwal.getJenisKelamin()));
                setoranAwalHajiRequest.setKodePekerjaan(String.valueOf(setoranAwal.getPekerjaan().getKodePekerjaan()));
                setoranAwalHajiRequest.setKodePendidikan(String.valueOf(setoranAwal.getPendidikan().getKodePendidikan()));
                setoranAwalHajiRequest.setKodeStatusPernikahan(String.valueOf(setoranAwal.getStatusKawin().getStatusKawinId()));
                setoranAwalHajiRequest.setNamaJemaah(setoranAwal.getNamaJemaah());
                setoranAwalHajiRequest.setNoIdentitas(setoranAwal.getNoIdentitas());
                setoranAwalHajiRequest.setTanggalLahir(new SimpleDateFormat("ddMMyyyy").format(setoranAwal.getTanggalLahir()));
                setoranAwalHajiRequest.setTempatLahir(setoranAwal.getTempatLahir());
                setoranAwalHajiRequest.setAlamat(setoranAwal.getAlamat());
                setoranAwalHajiRequest.setKodePos(setoranAwal.getKodePos());
                setoranAwalHajiRequest.setDesa(setoranAwal.getKelurahan());
                setoranAwalHajiRequest.setKecamatan(setoranAwal.getKecamatan());
                setoranAwalHajiRequest.setKabupatenKota(setoranAwal.getKabupatenKota());
                Cities city = citiesRepository.findByCityCodeCbs(setoranAwal.getKabupatenKotaId());
                setoranAwalHajiRequest.setKodeKabupatenKota(city.getCityCode());
                setoranAwalHajiRequest.setKodeProvinsi(city.getProvinces().getProvinceCode());
                setoranAwalHajiRequest.setNamaAyah(setoranAwal.getNamaAyah());

                SetoranAwalHajiData setoranAwalHajiData = new SetoranAwalHajiData();
                setoranAwalHajiData.setNoRekening(setoranAwal.getNoRekening());
                setoranAwalHajiData.setMerchantType(setoranAwal.getChannel().getKodeMerchant());
                setoranAwalHajiData.setSettlementDate(setoranAwal.getTanggalTransaksi());
                setoranAwalHajiData.setTerminalId(userBranchCode);
                setoranAwalHajiData.setTransactionAmount(setoranAwal.getNominalSetoran().toString() + "00");
                setoranAwalHajiData.setBranchCode(branchCode);
                setoranAwalHajiData.setOriginalBranchCode(setoranAwal.getBranchCode());
                setoranAwalHajiData.setSetoranAwalHajiRequest(setoranAwalHajiRequest);

                System.out.println("--------------- MULTICHANNEL SETORAN AWAL REQUEST -------------------");
                System.out.println(setoranAwalHajiData.toString());
                System.out.println("---------------------------------------------------------------------");

                String url = urlSwitchingApp + "api/switching_haji/pembayaran_setoran_awal";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<SetoranAwalHajiData> request = new HttpEntity<SetoranAwalHajiData>(setoranAwalHajiData, headers);
                String response = restTemplate.postForObject(url, request, String.class);
                JSONObject objectResponse = new JSONObject(response);

                System.out.println("--------------- MULTICHANNEL SETORAN AWAL RESPONSE -------------------");
                System.out.println(response);
                System.out.println("------------------------------------------------------------------------");

                if (objectResponse.getString("rc").equals("00")) {
                    SetoranAwalHajiResponse data = mapper.readValue(objectResponse.get("data").toString(), SetoranAwalHajiResponse.class);

                    setoranAwal.setNoValidasi(data.getNomorValidasi());
                    setoranAwal.setSystemTraceNumber(data.getStan());
                    setoranAwal.setRetRefNumber(data.getRetrievalReferenceNumber());
                    setoranAwal.setKloter(data.getKloter());
                    setoranAwal.setTahunBerangkat(data.getTahunBerangkat());
                    setoranAwal.setEmbarkasi(data.getEmbarkasi());
                    setoranAwal.setStatusTransaksi(new StatusTransaksi(3));
                    setoranAwal.setVirtualAccount(data.getVirtualAccount());
                    setoranAwal.setUpdatedDate(new Date());
                    setoranAwal.setUpdatedBy(userCode);

                    SetoranAwal result = setoranAwalRepository.save(setoranAwal);

                    return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), result, objectResponse.getString("message")));
                } else{
                    return ResponseEntity.ok().body(new Response(objectResponse.getString("rc"), null, objectResponse.getString("message")));
                }
            } else{
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal todak ditemukan"));
            }
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response("99", null, e.getLocalizedMessage()));
        }
    }

    @PostMapping("/reversal-multichannel")
    public Object reversalMultichannel(@RequestParam("setoranAwalId") long setoranAwalId, @RequestParam("userCode") String userCode) {
        try {
            SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranAwalId).orElse(null);
            if(setoranAwal != null) {
                AwalReversalHistory awalReversalHistory = new AwalReversalHistory();
                awalReversalHistory.setSetoranAwalId(setoranAwal.getSetoranAwalId());
                awalReversalHistory.setNoRekening(setoranAwal.getNoRekening());
                awalReversalHistory.setNamaJemaah(setoranAwal.getNamaJemaah());
                awalReversalHistory.setNominalSetoran(setoranAwal.getNominalSetoran());
                awalReversalHistory.setNoArsip(setoranAwal.getTransactionId());
                awalReversalHistory.setTanggalReversal(new Date());
                awalReversalHistory.setBranchCode(setoranAwal.getBranchCode());
                awalReversalHistory.setCreatedBy(userCode);
                awalReversalHistory.setCreatedDate(new Date());
                awalReversalHistory.setUpdatedBy(userCode);
                awalReversalHistory.setUpdatedDate(new Date());
                awalReversalHistory.setStatusActive(false);
                AwalReversalHistory resultReversal = awalReversalHistoryRepository.save(awalReversalHistory);
                setoranAwalRepository.delete(setoranAwal);
                return ResponseEntity.ok().body(new Response("00", resultReversal, "Reversal Setoran Awal berhasil"));
            } else {
                return ResponseEntity.ok().body(new Response("99", null, "Data Setoran Awal tidak ditemukan"));
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