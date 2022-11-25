package com.bjbs.haji.business.apis.controllers;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.apis.dtos.SetoranAwalDTO;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.service.SetoranAwalService;
import com.io.iona.implementations.data.persistent.DefaultReadAllResult;
import com.io.iona.springboot.controllers.HibernateCRUDController;

@RestController
@RequestMapping("/api/setoran-awal")
public class SetoranAwalController extends HibernateCRUDController<SetoranAwal, SetoranAwalDTO> {

	private static final Log log = LogFactory.getLog(SetoranAwalController.class);
	
@Autowired
private SetoranAwalService setoranAwalService;

@PutMapping("/{setoranAwalId}")
public ResponseEntity<Response> updateDataSetoranAwal(@PathVariable Long setoranAwalId, 
		@RequestBody SetoranAwalDTO body){
	log.info("UPDATE DATA SETORAN AWAL");
	
	Response response = setoranAwalService.updateDataSetoranAwal(setoranAwalId,body);
	
	if(response.getRC().equals("00")) {
		return ResponseEntity.ok(response);
	}else {
		return ResponseEntity.badRequest().body(response);
	}
	
}
	
	
//    @Override
//    public DefaultReadAllResult ionaReadAllEndpoint(@RequestParam String filter, @RequestParam String orderby, @RequestParam("top") Integer pageTotalRows, @RequestParam("skip") Integer pageNumber, @RequestParam Map<String, String> requestParameters, @RequestHeader Map<String, String> requestHeaders) throws Exception {
//        return super.ionaReadAllEndpoint(filter, orderby, pageTotalRows, pageNumber, requestParameters, requestHeaders);
//	}
//
//	@GetMapping("/readForSP")
//	public DefaultReadAllResult readForSatisfactionPayment(@RequestParam String filter, @RequestParam String orderby, @RequestParam("top") Integer pageTotalRows, @RequestParam("skip") Integer pageNumber, @RequestParam Map<String, String> requestParameters, @RequestHeader Map<String, String> requestHeaders) throws Exception {
//		String filterAdditions="statusTransaksi.statusId=6";
//		filter+=(!filter.trim().isEmpty())?" AND "+filterAdditions:filterAdditions;
//		return this.ionaReadAllEndpoint(filter, orderby, pageTotalRows, pageNumber, requestParameters, requestHeaders);
//	}

//    @Override
//    public Object ionaInsertEndpoint(@RequestBody PembayaranAwalDTO requestBody, @RequestHeader Map<String, String> requestHeaders) throws Exception {
//        boolean isAllCorrect=
//            requestBody.getBranchId()!=null &&
//            requestBody.getTipeHaji()!=null &&
//            requestBody.getJumlahPembayaran()!=null &&
//            requestBody.getNoRekening()!=null &&
//            requestBody.getNamaNasabah()!=null &&
//            requestBody.getNamaAyah()!=null &&
//            requestBody.getNoIdentitas()!=null &&
//            requestBody.getTempatLahir()!=null &&
//            requestBody.getTglLahir()!=null &&
//            requestBody.getJenisKelamin()!=null &&
//            requestBody.getAlamat()!=null &&
//            requestBody.getKodePos()!=null &&
//            requestBody.getKelurahan()!=null &&
//            requestBody.getKecamatan()!=null &&
//            requestBody.getKabKota()!=null &&
//            requestBody.getPekerjaan()!=null &&
//            requestBody.getStatusKawin()!=null &&
//            requestBody.getCreatedBy()!=null &&
//            requestBody.getUpdatedBy()!=null;
//
//        if(isAllCorrect){
//            //LocalDate nowDate=LocalDate.now();
//            LocalDateTime nowDateTime=LocalDateTime.now();
//            Date nowDateOldFashioned=Date.from(nowDateTime.atZone(ZoneId.systemDefault()).toInstant());
//
//            Map<String, String> dummyParam=new LinkedHashMap<>();
//            dummyParam.put("filter", ""); dummyParam.put("orderby", ""); dummyParam.put("top", "0"); dummyParam.put("skip", "0");
//            DefaultReadAllResult result=this.ionaReadAllEndpoint("", "", 0, 0, dummyParam, requestHeaders);
//
//            String systemTraceNumber=String.format("%06d", result.getTotalItems()+1);
//
//            int nowMonth=nowDateTime.getMonthValue();
//            char nowMonthChar=(nowMonth<10)?String.valueOf(nowMonth).charAt(0):(nowMonth==10)?'A':(nowMonth==11)?'B':(nowMonth==12)?'C':'?';
//            requestBody.setRetRefNumber("425"+nowMonthChar+String.format("%02d", nowDateTime.getDayOfMonth())+systemTraceNumber);
//            requestBody.setSystemTraceNumber(Long.parseLong(systemTraceNumber));
//            requestBody.setTglTransaksi(nowDateOldFashioned);
//            requestBody.setJamTransaksi(nowDateOldFashioned);
//            //requestBody.setEmbarkasi("JKG");
//            //requestBody.setKloter("109");
//            //requestBody.setSisaPembayaran(new BigDecimal(15000000));
//			requestBody.setNoArsip("s114500001");
//			requestBody.setStatusTransaksi(new StatusTransaksiDTO(1));
//
//            requestBody.setCreatedDate(nowDateOldFashioned);
//            requestBody.setUpdatedBy(null);
//
//            //return requestBody;
//            return super.ionaInsertEndpoint(requestBody, requestHeaders);
//        }else{
//            LinkedHashMap<String, Object> response=new LinkedHashMap<>();
//
//            response.put("message", "data gagal disimpan!");
//			response.put("cause", "data yang diterima tidak lengkap!");
//			response.put("body", requestBody);
//
//            return ResponseEntity.badRequest().body(response);
//        }
//	}
    
    /*
	@Autowired ModelMapper modelMapper;
	@PutMapping("/approve-old")
	public Object approve(@RequestBody Map<String, Object> requestBody, @RequestHeader Map<String, String> requestHeaders) throws Exception {

		Long id=Long.parseLong(requestBody.get("id").toString());

		Map<String, String> dummyParam=new LinkedHashMap<>();
		dummyParam.put("filter", "pembayaranAwalId="+id); dummyParam.put("orderby", ""); dummyParam.put("top", "1"); dummyParam.put("skip", "0");
        DefaultReadAllResult result=this.ionaReadAllEndpoint("pembayaranAwalId="+id, "", 1, 0, dummyParam, requestHeaders);
        
        return result.getItems().get(0);

		/*
		if(result.getItems().size()>0){
			//PembayaranAwal pa=modelMapper.map(result.getItems().get(0), PembayaranAwal.class);
			//PembayaranAwalDTO pad=modelMapper.map(pa, PembayaranAwalDTO.class);

            //pad.setStatusTransaksi(new StatusTransaksiDTO(2));

			dummyParam.clear();
			dummyParam.put("modelID", "pembayaranAwalId:"+id);

			//return pad;
			//return result.getItems().get(0);

			return this.ionaUpdateEndpoint(pad, dummyParam, requestHeaders);
		}else{
			LinkedHashMap<String, Object> response=new LinkedHashMap<>();

            response.put("message", "data gagal disimpan!");
			response.put("cause", "data tidak ditemukan!");
			response.put("body", requestBody);

            return ResponseEntity.badRequest().body(response);
		}
		//

    }
    //*/

}
