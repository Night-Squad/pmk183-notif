package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.controllers.utility.CustomMultipartFile;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadBuktiController {

    @Value("${url.app}")
    String siskohatUrl;

    @Autowired
    CetakResiSetoranAwalController cetakResiSetoranAwalController;

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @GetMapping("/bukti-setoran-awal")
    public Object upload(@RequestParam("noValidasi") String noValidasi,
                         @RequestHeader("token-kemenag") String token){
        String url = siskohatUrl + "siskohat/bank/setoranawal";
        try {

            Map<String, String> parameter = new HashMap<>();
            parameter.put("noValidasi", noValidasi);
            Map<String, String> header = new HashMap<>();
            SetoranAwalDTO usersDTO = new SetoranAwalDTO();
            ResponseEntity<byte[]> responseFile = cetakResiSetoranAwalController.ionaGenerateAsPDF(usersDTO, parameter, header);

            String filename = "bukti-setoran-awal-" + LocalDateTime.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() + ".pdf";
            CustomMultipartFile multipartFile = new
                    CustomMultipartFile(responseFile.getBody(), "application/pdf", filename);
            multipartFile.transferTo(multipartFile.getFile());

            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("no_validasi", noValidasi);
            map.add("upload", new FileSystemResource(multipartFile.getFile()));

            System.out.println("----------------------------------------------------------------------");
            System.out.println("No Validasi : " + map.get("no_validasi"));
            System.out.println("File : " + multipartFile.getName());
            System.out.println("----------------------------------------------------------------------");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("x-access-key", token);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            System.out.println("----------------------------------------------------------------------");
            System.out.println(jsonResponse.toString());
            System.out.println("----------------------------------------------------------------------");
            if (jsonResponse.getString("RC").equals("00") || jsonResponse.getString("RC").equals("29")) {
                SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoValidasi(noValidasi);
                setoranAwal.setIsUploaded(true);
                setoranAwalRepository.save(setoranAwal);
            }
            return response;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.out.println(e.getResponseBodyAsString());
            return ResponseEntity.badRequest().body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
