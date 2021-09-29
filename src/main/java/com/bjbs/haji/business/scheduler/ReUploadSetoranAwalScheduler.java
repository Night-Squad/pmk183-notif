package com.bjbs.haji.business.scheduler;

import com.bjbs.haji.business.apis.controllers.CetakResiSetoranAwalController;
import com.bjbs.haji.business.apis.controllers.utility.CustomMultipartFile;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.utility.HajiConstant;
import com.netflix.discovery.converters.Auto;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReUploadSetoranAwalScheduler {

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    CetakResiSetoranAwalController cetakResiSetoranAwalController;

    @Value("${url.app}")
    String siskohatUrl;

    Logger logger = LoggerFactory.getLogger(ReUploadSetoranAwalScheduler.class);

    RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelayString = "${fixedDelay.reupload}")
    public void reUploadSetoranAwal() {
        logger.info("==================================== RE UPLOAD BATCH START =========================================");

        String token = null;
        try {

            String url = siskohatUrl + "umrah/bank/login";

            HttpHeaders headers = new HttpHeaders();

            headers.set("x-key", "98erk34dj");
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> kemenag = new HashMap<String, Object>();

            HttpEntity<String> body = new HttpEntity<String>(HajiConstant.BODY_LOGIN_KEMENAG, headers);
            String responseKemenag = restTemplate.postForObject(url, body, String.class);
            JSONObject obj = new JSONObject(responseKemenag);

            if (obj.get("RC").equals("00")) {
                token = obj.get("token").toString();
            }

            List<SetoranAwal> setoranAwals = setoranAwalRepository.getSetoranAwalNotUploaded();
            for (SetoranAwal setoranAwal : setoranAwals) {

                String uploadUrl = siskohatUrl + "siskohat/bank/setoranawal";
                try {

                    Map<String, String> parameter = new HashMap<>();
                    parameter.put("noValidasi", setoranAwal.getNoValidasi());
                    Map<String, String> header = new HashMap<>();
                    SetoranAwalDTO usersDTO = new SetoranAwalDTO();
                    ResponseEntity<byte[]> responseFile = cetakResiSetoranAwalController.ionaGenerateAsPDF(usersDTO, parameter, header);

                    String filename = "bukti-setoran-awal-" + LocalDateTime.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() + ".pdf";
                    CustomMultipartFile multipartFile = new
                            CustomMultipartFile(responseFile.getBody(), "application/pdf", filename);
                    multipartFile.transferTo(multipartFile.getFile());

                    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
                    map.add("no_validasi", setoranAwal.getNoValidasi());
                    map.add("upload", new FileSystemResource(multipartFile.getFile()));

                    logger.info("------------------------------------------------------------------------------");
                    logger.info("No Validasi : " + map.get("no_validasi"));
                    logger.info("File : " + multipartFile.getName());

                    HttpHeaders uploadHeaders = new HttpHeaders();
                    uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                    uploadHeaders.set("x-access-key", token);
                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, uploadHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
                    JSONObject jsonResponse = new JSONObject(response.getBody());
                    logger.info(jsonResponse.toString());
                    if (jsonResponse.getString("RC").equals("00") || jsonResponse.getString("RC").equals("29")) {
                        setoranAwal.setIsUploaded(true);
                        setoranAwalRepository.save(setoranAwal);
                    }
                } catch (HttpClientErrorException e) {
                    logger.info(e.getResponseBodyAsString());
                }
                logger.info("------------------------------------------------------------------------------");
            }
        } catch (Exception e) {
            logger.info("SOMETHING WENT WRONG");
            e.printStackTrace();
        }
        logger.info("==================================== RE UPLOAD BATCH END ===========================================");
    }

}
