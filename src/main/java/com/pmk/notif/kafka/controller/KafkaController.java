// package com.pmk.notif.kafka.controller;
//
// import com.bjbs.haji.business.apis.dtos.Response;
// import com.bjbs.haji.business.apis.dtos.SetoranAwalHajiRequest;
// import com.pmk.notif.kafka.dto.SetoranAwalDataKafkaResponse;
// import com.bjbs.haji.business.views.dtos.kafka.SetoranAwalHajiDataKafka;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.google.gson.Gson;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;
//
// import java.text.SimpleDateFormat;
// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;
//
// @RestController
// public class KafkaController {
//
// 	@Value("${kafka-topic}")
// 	private String kafkaTopic;
//
//	 @Autowired
//    CitiesRepository citiesRepository;
//
// 	@Autowired
//    SetoranAwalRepository setoranAwalRepository;
//
//	@Autowired
//	StatusTransaksiRepository statusTransaksiRepository;
//
//	ObjectMapper mapper = new ObjectMapper();
//
//
// 	@Autowired
// 	private KafkaTemplate<String, String> kafkaTemplate;
//
// 	@PostMapping(value = "/repo/setoran-awal/v2/pembayaran", consumes = "application/json", produces = "application/json")
// 	public Response sendMessageIncoming(@RequestBody SetoranAwalHajiDataKafka message) throws Exception {
//		Response response = new Response();
//        Map<String, Object> result = new HashMap<>();
//		System.out.println("Request Body = "+message);
//		LocalDateTime date = LocalDateTime.now();
//		try{
//		SetoranAwal setoranAwal = setoranAwalRepository.findById(message.getSetoranAwalId()).orElse(null);
//		System.out.println(""+ setoranAwal);
//            if (setoranAwal != null) {
//                SetoranAwalHajiRequest setoranAwalHajiRequest = new SetoranAwalHajiRequest();
//                setoranAwalHajiRequest.setJenisHaji(setoranAwal.getTipeHaji().getKodeHaji());
//                setoranAwalHajiRequest.setJenisKelamin(String.valueOf(setoranAwal.getJenisKelamin()));
//                setoranAwalHajiRequest.setKodePekerjaan(String.valueOf(setoranAwal.getPekerjaan().getKodePekerjaan()));
//                setoranAwalHajiRequest.setKodePendidikan(String.valueOf(setoranAwal.getPendidikan().getKodePendidikan()));
//                setoranAwalHajiRequest.setKodeStatusPernikahan(String.valueOf(setoranAwal.getStatusKawin().getStatusKawinKemenag()));
//                setoranAwalHajiRequest.setNamaJemaah(setoranAwal.getNamaJemaah());
//                setoranAwalHajiRequest.setNoIdentitas(setoranAwal.getNoIdentitas());
//                setoranAwalHajiRequest.setTanggalLahir(new SimpleDateFormat("ddMMyyyy").format(setoranAwal.getTanggalLahir()));
//                setoranAwalHajiRequest.setTempatLahir(setoranAwal.getTempatLahir());
//                setoranAwalHajiRequest.setAlamat(setoranAwal.getAlamat());
//                setoranAwalHajiRequest.setKodePos(setoranAwal.getKodePos());
//                setoranAwalHajiRequest.setDesa(setoranAwal.getKelurahan());
//                setoranAwalHajiRequest.setKecamatan(setoranAwal.getKecamatan());
//                setoranAwalHajiRequest.setKabupatenKota(setoranAwal.getKabupatenKota());
//
//                Cities city = citiesRepository.findByCityCodeCbs(setoranAwal.getKabupatenKotaId());
//				if(city == null){
//					response.setMessage("Mohon Periksa Kembali Data Setoran Awal ID atau City Code CBS");
//					response.setRC("53");
//					return response;
//				}
//                setoranAwalHajiRequest.setKodeKabupatenKota(city.getCityCode());
//                setoranAwalHajiRequest.setKodeProvinsi(city.getProvinces().getProvinceCode());
//                setoranAwalHajiRequest.setNamaAyah(setoranAwal.getNamaAyah());
//
//                SetoranAwalDataKafkaResponse setoranAwalHajiData = new SetoranAwalDataKafkaResponse();
//                setoranAwalHajiData.setNoRekening(setoranAwal.getNoRekening());
//                setoranAwalHajiData.setMerchantType(setoranAwal.getChannel().getKodeMerchant());
//                setoranAwalHajiData.setSettlementDate(setoranAwal.getTanggalTransaksi());
//                setoranAwalHajiData.setTerminalId(message.getUserBranchCode());
//                setoranAwalHajiData.setTransactionAmount(setoranAwal.getNominalSetoran().toString() + "00");
//                setoranAwalHajiData.setBranchCode(message.getBranchCode());
//                setoranAwalHajiData.setSetoranAwalHajiRequest(setoranAwalHajiRequest);
//                setoranAwalHajiData.setUserCode(message.getUserCode());
//                setoranAwalHajiData.setSetoranAwalId(message.getSetoranAwalId());
//                setoranAwalHajiData.setTokenKemenag(message.getTokenKemenag());
//                setoranAwalHajiData.setTimestamp(date.toString());
//
//				Optional<SetoranAwal> exitingSetoranAwal = setoranAwalRepository.findById(message.getSetoranAwalId());
//				if(!exitingSetoranAwal.isPresent()){
//					response.setMessage("Data Setoran Awal ID Tidak Ada");
//					response.setRC("53");
//					return response;
//				}
//
//				SetoranAwal setoranAwalUpdate = exitingSetoranAwal.get();
//
//				StatusTransaksi statusTransaksi = statusTransaksiRepository.findById((long) 7).orElse(null);
//				setoranAwalUpdate.setStatusTransaksi(statusTransaksi);
//
//				setoranAwalRepository.save(setoranAwalUpdate);
//
//                System.out.println("------------------ REQUEST BODY SWITCHING PEMBAYARAN SETORAN AWAL ------------------");
//                System.out.println(mapper.writeValueAsString(setoranAwalHajiData));
//                System.out.println("------------------------------------------------------------------------------------");
//
//				message.setTimestap(LocalDateTime.now().toString());
//
////				Map<String,Object> result = new HashMap<>();
////				result.put("data", setoranAwalHajiData);
////				result.put("body",message);
//
//				response.setRC("00");
//				response.setData(setoranAwalHajiData);
//				response.setMessage("Setoran Awal dengan Nomor Rekening "+ setoranAwal.getNoRekening() +" sedang di proses");
//				// Sending the message to kafka topic queue
//				System.out.println("sending chat...");
//				System.out.println("chat : " + message.toString());
//				kafkaTemplate.send("setoran_awal_incoming", "setoran-awal", new Gson().toJson(response));
//
//				System.out.println("+=================================================================");
//				return response;
// 			}else{
//				response.setMessage("Mohon Periksa Kembali Data Setoran Awal ID");
//				response.setRC("53");
//				return response;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//				response.setRC("99");
//				response.setData(e.getLocalizedMessage());
//				response.setMessage(null);
//				return response;
//		}
//	}
// }
