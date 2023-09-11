package com.bjbs.haji.business.kafka.component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bjbs.haji.business.apis.controllers.CetakResiSetoranAwal2Controller;
import com.bjbs.haji.business.apis.controllers.utility.CustomMultipartFile;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.kafka.dto.SetoranAwalHajiResponseKafka;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.google.gson.Gson;
import com.bjbs.haji.business.kafka.dto.SetoranAwalHajiResponseKafka;

@Component
public class KafkaListeners {

	@Autowired
	private SetoranAwalRepository setoranAwalRepository;

	@Value("${url.app}")
	private String siskohatUrl;
	
	@Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
	
    @Autowired
    CetakResiSetoranAwal2Controller cetakResiSetoranAwalController;

	@KafkaListener(topics = "setoran_awal_incoming", groupId = "setoran-awal", topicPartitions = {
			@TopicPartition(topic = "setoran_awal_incoming", partitions = { "0", "1", "2" }) })
	void listener(String data) {
		System.out.println("Listener received incoming:  " + data);
//		template.convertAndSend("/topic/setoran_awal_incoming", data);
	}

	@KafkaListener(topics = "setoran_awal_outgoing", groupId = "setoran-awal", topicPartitions = {
			@TopicPartition(topic = "setoran_awal_outgoing", partitions = { "0", "1", "2" }) })

	void listenerOutgoing(String data) {
		System.out.println("Listener received outgoing:  " + data);

		Gson gson = new Gson();
		SetoranAwalHajiResponseKafka setoranAwalHajiResponseKafka = gson.fromJson(data,
				SetoranAwalHajiResponseKafka.class);

		Long setoranID = Long.parseLong(setoranAwalHajiResponseKafka.getSetoranAwalId());
		SetoranAwal setoranAwal = setoranAwalRepository.findById(setoranID).orElse(null);

		if (setoranAwal != null) {
			if (setoranAwalHajiResponseKafka.getRc().equals("00")) {
				setoranAwal.setNoValidasi(setoranAwalHajiResponseKafka.getNomorValidasi());
				setoranAwal.setSystemTraceNumber(setoranAwalHajiResponseKafka.getStan());
				setoranAwal.setRetRefNumber(setoranAwalHajiResponseKafka.getRetrievalReferenceNumber());
				setoranAwal.setKloter(setoranAwalHajiResponseKafka.getKloter());
				setoranAwal.setTahunBerangkat(setoranAwalHajiResponseKafka.getTahunBerangkat());
				setoranAwal.setEmbarkasi(setoranAwalHajiResponseKafka.getEmbarkasi());
				setoranAwal.setStatusTransaksi(new StatusTransaksi(3));
				setoranAwal.setVirtualAccount(setoranAwalHajiResponseKafka.getVirtualAccount());
				setoranAwal.setUpdatedDate(new Date());
				setoranAwal.setUpdatedBy(setoranAwalHajiResponseKafka.getUserCode());
				setoranAwalRepository.save(setoranAwal);

				System.out.println(
						"--------------VALIDATION SEND BUKTI RESI BY NOMOR REKENING REQUEST AND RESPONSE----------");
				System.out.println("setoranAwal.getNoRekening() : " + setoranAwal.getNoRekening());
				System.out.println("response.getNomorRekening() : " + setoranAwalHajiResponseKafka.getNomorRekening());
				System.out.println(
						"-----------------------------------------------------------------------------------------");

				if (setoranAwal.getNoRekening().equals(setoranAwalHajiResponseKafka.getNomorRekening())) {
					System.out.println("rekening is the same...");
					Map<String, Object> resultUpload = new HashMap<>();

					try {
						String uploadBuktiSetoranUrl = siskohatUrl + "siskohat/bank/setoranawal";

						Map<String, String> parameter = new HashMap<>();
						parameter.put("setoranAwalId", setoranAwal.getSetoranAwalId().toString());
						Map<String, String> header = new HashMap<>();
						SetoranAwalDTO usersDTO = new SetoranAwalDTO();
						ResponseEntity<byte[]> responseFile = cetakResiSetoranAwalController.ionaGenerateAsPDF(usersDTO,
								parameter, header);

						String filename = "bukti-setoran-awal-"
								+ LocalDateTime.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() + ".pdf";
						CustomMultipartFile multipartFile = new CustomMultipartFile(responseFile.getBody(),
								"application/pdf", filename);
						multipartFile.transferTo(multipartFile.getFile());

						MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
						map.add("no_validasi", setoranAwalHajiResponseKafka.getNomorValidasi());
						map.add("upload", new FileSystemResource(multipartFile.getFile()));

						System.out.println(
								"------------------------- REQUEST BODY BUKTI SETORAN AWAL ------------------");
						System.out.println("No Validasi : " + map.get("no_validasi"));
						System.out.println("File : " + multipartFile.getName());
						System.out.println("token kemenag : " + setoranAwalHajiResponseKafka.getTokenKemenag());
						System.out.println(
								"----------------------------------------------------------------------------");

						RestTemplate restTemplateUpload = new RestTemplate();
						HttpHeaders headersUpload = new HttpHeaders();
						headersUpload.setContentType(MediaType.MULTIPART_FORM_DATA);
						headersUpload.set("x-access-key", setoranAwalHajiResponseKafka.getTokenKemenag());
						HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headersUpload);

						ResponseEntity<String> responseUpload = restTemplateUpload.postForEntity(uploadBuktiSetoranUrl,
								requestEntity, String.class);

						resultUpload.put("uploadBukti", responseUpload.getBody());
						JSONObject jsonResultUpload = new JSONObject(responseUpload.getBody());
						System.out.println(
								"------------------------- RESPONSE UPLOAD BUKTI SETORAN AWAL ------------------");
						System.out.println(jsonResultUpload.toString());
						System.out.println(
								"-------------------------------------------------------------------------------");
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
				} else {
					System.out.println("rekening is not the same..");
				}
                System.out.println("--------------VALIDATION SEND BUKTI RESI BY NOMOR REKENING REQUEST AND RESPONSE----------");

                kafkaTemplate.send("setoran_awal_outgoing", "setoran-awal", new Gson().toJson(setoranAwalHajiResponseKafka));
			}
		}else {
			System.out.println("setoran awal id null...");
		}
	}
}
