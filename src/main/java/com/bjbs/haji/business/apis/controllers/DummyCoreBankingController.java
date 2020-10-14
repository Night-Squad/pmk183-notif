package com.bjbs.haji.business.apis.controllers;

import java.util.LinkedHashMap;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dummy-core-banking")
public class DummyCoreBankingController {

    static class AccountResponse {
    	
    	public AccountResponse() {};
    	
        public AccountResponse(String no_rekening, String nama, String nama_ayah, String nomor_identitas,
				String tempat_lahir, String tgl_lahir, String jenis_kelamin, String alamat, String kode_pos,
				String desa, String kecamatan, String kabupaten, String provinsi, String pekerjaan, String pendidikan,
				String status_pernikahan) {
			super();
			this.no_rekening = no_rekening;
			this.nama = nama;
			this.nama_ayah = nama_ayah;
			this.nomor_identitas = nomor_identitas;
			this.tempat_lahir = tempat_lahir;
			this.tgl_lahir = tgl_lahir;
			this.jenis_kelamin = jenis_kelamin;
			this.alamat = alamat;
			this.kode_pos = kode_pos;
			this.desa = desa;
			this.kecamatan = kecamatan;
			this.kabupaten = kabupaten;
			this.provinsi = provinsi;
			this.pekerjaan = pekerjaan;
			this.pendidikan = pendidikan;
			this.status_pernikahan = status_pernikahan;
		}

		private String no_rekening;
        private String nama;
        private String nama_ayah;
        private String nomor_identitas;
        private String tempat_lahir;
        private String tgl_lahir;
        private String jenis_kelamin;
        private String alamat;
        private String kode_pos;
        private String desa;
        private String kecamatan;
        private String kabupaten;
        private String provinsi;
        private String pekerjaan;
        private String pendidikan;
        private String status_pernikahan;
        
		public String getNo_rekening() {
			return no_rekening;
		}
		public void setNo_rekening(String no_rekening) {
			this.no_rekening = no_rekening;
		}
		public String getNama() {
			return nama;
		}
		public void setNama(String nama) {
			this.nama = nama;
		}
		public String getNama_ayah() {
			return nama_ayah;
		}
		public void setNama_ayah(String nama_ayah) {
			this.nama_ayah = nama_ayah;
		}
		public String getNomor_identitas() {
			return nomor_identitas;
		}
		public void setNomor_identitas(String nomor_identitas) {
			this.nomor_identitas = nomor_identitas;
		}
		public String getTempat_lahir() {
			return tempat_lahir;
		}
		public void setTempat_lahir(String tempat_lahir) {
			this.tempat_lahir = tempat_lahir;
		}
		public String getTgl_lahir() {
			return tgl_lahir;
		}
		public void setTgl_lahir(String tgl_lahir) {
			this.tgl_lahir = tgl_lahir;
		}
		public String getJenis_kelamin() {
			return jenis_kelamin;
		}
		public void setJenis_kelamin(String jenis_kelamin) {
			this.jenis_kelamin = jenis_kelamin;
		}
		public String getAlamat() {
			return alamat;
		}
		public void setAlamat(String alamat) {
			this.alamat = alamat;
		}
		public String getKode_pos() {
			return kode_pos;
		}
		public void setKode_pos(String kode_pos) {
			this.kode_pos = kode_pos;
		}
		public String getDesa() {
			return desa;
		}
		public void setDesa(String desa) {
			this.desa = desa;
		}
		public String getKecamatan() {
			return kecamatan;
		}
		public void setKecamatan(String kecamatan) {
			this.kecamatan = kecamatan;
		}
		public String getKabupaten() {
			return kabupaten;
		}
		public void setKabupaten(String kabupaten) {
			this.kabupaten = kabupaten;
		}
		public String getProvinsi() {
			return provinsi;
		}
		public void setProvinsi(String provinsi) {
			this.provinsi = provinsi;
		}
		public String getPekerjaan() {
			return pekerjaan;
		}
		public void setPekerjaan(String pekerjaan) {
			this.pekerjaan = pekerjaan;
		}
		public String getPendidikan() {
			return pendidikan;
		}
		public void setPendidikan(String pendidikan) {
			this.pendidikan = pendidikan;
		}
		public String getStatus_pernikahan() {
			return status_pernikahan;
		}
		public void setStatus_pernikahan(String status_pernikahan) {
			this.status_pernikahan = status_pernikahan;
		}
        
    }

    static class AccountRequest {

        private String no_rekening;

		public String getNo_rekening() {
			return no_rekening;
		}

		public void setNo_rekening(String no_rekening) {
			this.no_rekening = no_rekening;
		}
        
    }
    
    @PostMapping("/get-account")
    public LinkedHashMap<?, ?> getAccout(@RequestBody AccountRequest requestBody){
        LinkedHashMap<String, AccountResponse> accountMap=new LinkedHashMap<>();

        accountMap.put(
            "123234345456567",
            new AccountResponse(
                "1232343454565676",
                "NAMA NASABAH INI",
                "NAMA AYAH INI",
                "161112665234",
                "JAKARTA",
                "2001-05-06",
                "2",
                "JL. JALAN KE MANA",
                "40523",
                "KARANGMEKAR",
                "CIMAHI TENGAH",
                "1",
                "1",
                "1",
                "1",
                "1"
            )
        );

        accountMap.put(
            "0876907249",
            new AccountResponse(
                "0876907249",
                "MOCHAMAD SEFTIKARA AL MAYASIR SOETIAWARMAN",
                "UNANG SUTIAWARMAT HIDAYAT",
                "161112665234",
                "JAKARTA",
                "2001-05-06",
                "1",
                "JL. RADEN EMBANG ARTAWIDJAJA NO. 12",
                "40523",
                "KARANGMEKAR",
                "CIMAHI TENGAH",
                "1",
                "2",
                "1",
                "2",
                "1"
            )
        );

        LinkedHashMap<String, Object> response=new LinkedHashMap<>();
        AccountResponse existingAccount=accountMap.get(requestBody.getNo_rekening());

        response.put("RC", 00);
        if(existingAccount!=null){
            response.put("message", "berhasil");
            response.put("data", existingAccount);
        }else{
            response.put("message", "gagal mendapatkan data!");
        }

        return response;
    }

}