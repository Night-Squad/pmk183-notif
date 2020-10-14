package com.bjbs.haji.business.repositories.controllers;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.models.StatusTransaksi;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api/view_dashboard_haji")
public class ViewDashboardHajiController {
	
	@Autowired
	SetoranAwalRepository setoranAwalRepository;
	
	@Autowired
	SetoranPelunasanRepository setoranPelunasanRepository;
	
	@GetMapping("/readAll")
	public Object viewDashboardUmroh(@RequestParam String kdCabang, @RequestParam String tglAwal, @RequestParam String tglAkhir) {
		LocalDate localDateTglAwal = LocalDate.parse(tglAwal);
		LocalDate localDateTglAkhir = LocalDate.parse(tglAkhir).plusDays(1);
		
		Date dateTglAwal = Date.from(localDateTglAwal.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dateTglAkhir = Date.from(localDateTglAkhir.atStartOfDay(ZoneId.systemDefault()).toInstant());

		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> setoranAwal = new HashMap<String, Object>();
		Map<String, Object> setoranPelunasan = new HashMap<String, Object>();
		Map<String, Object> allProduct = new HashMap<String, Object>();
		try {
			if (kdCabang.equals("000")) {
				long totalMenungguPersetujuanSA = setoranAwalRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(1), dateTglAwal, dateTglAkhir);
				long totalDisetujuiSA = setoranAwalRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(2), dateTglAwal, dateTglAkhir);
				long totalLunasSA = setoranAwalRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(3), dateTglAwal, dateTglAkhir);
				long totalDitolakSA = setoranAwalRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(4), dateTglAwal, dateTglAkhir);
				long totalMenungguReversalSA = setoranAwalRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(6), dateTglAwal, dateTglAkhir);
				long totalTransaksiSA = setoranAwalRepository.getCountBetweenDate(dateTglAwal, dateTglAkhir);
				Long totalNominalMasukSA = setoranAwalRepository.getNominalSetoran(dateTglAwal, dateTglAkhir) == null ? 0 : setoranAwalRepository.getNominalSetoran(dateTglAwal, dateTglAkhir);
				long jumlahNasabahSA = setoranAwalRepository.getCountBetweenDate(dateTglAwal, dateTglAkhir);
				
				float dividerSA = totalTransaksiSA == 0 ? 1 : (float) totalTransaksiSA;
				
				float persentaseMenungguPersetujuanSA = ((float)totalMenungguPersetujuanSA * 100) / dividerSA;
				float persentaseDisetujuiSA = ((float)totalDisetujuiSA * 100) / dividerSA;
				float persentaseLunasSA = ((float)totalLunasSA * 100) / dividerSA;
				float persentaseDitolakSA = ((float)totalDitolakSA * 100) / dividerSA;
				float persentaseMenungguReversalSA = ((float)totalMenungguReversalSA * 100) / dividerSA;

				setoranAwal.put("totalMenungguPersetujuan", totalMenungguPersetujuanSA);
				setoranAwal.put("totalDisetujui", totalDisetujuiSA);
				setoranAwal.put("totalLunas", totalLunasSA);
				setoranAwal.put("totalDitolak", totalDitolakSA);
				setoranAwal.put("totalMenungguReversal", totalMenungguReversalSA);
				setoranAwal.put("totalTransaksi", totalTransaksiSA);
				setoranAwal.put("totalNominalMasuk", totalNominalMasukSA);
				setoranAwal.put("jumlahNasabah", jumlahNasabahSA);
				setoranAwal.put("persentaseMenungguPersetujuan", persentaseMenungguPersetujuanSA);
				setoranAwal.put("persentaseDisetujui", persentaseDisetujuiSA);
				setoranAwal.put("persentaseLunas", persentaseLunasSA);
				setoranAwal.put("persentaseDitolak", persentaseDitolakSA);
				setoranAwal.put("persentaseMenungguReversal", persentaseMenungguReversalSA);

				long totalMenungguPersetujuanSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(1), dateTglAwal, dateTglAkhir);
				long totalDisetujuiSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(2), dateTglAwal, dateTglAkhir);
				long totalLunasSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(3), dateTglAwal, dateTglAkhir);
				long totalDitolakSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(4), dateTglAwal, dateTglAkhir);
				long totalMenungguReversalSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDate(new StatusTransaksi(6), dateTglAwal, dateTglAkhir);
				long totalTransaksiSP = setoranPelunasanRepository.getCountBetweenDate(dateTglAwal, dateTglAkhir);;
				Long totalNominalMasukSP = setoranPelunasanRepository.getNominalSetoran(dateTglAwal, dateTglAkhir) == null ? 0 : setoranPelunasanRepository.getNominalSetoran(dateTglAwal, dateTglAkhir);
				long jumlahNasabahSP = setoranPelunasanRepository.getCountBetweenDate(dateTglAwal, dateTglAkhir);;

				float dividerSP = totalTransaksiSP == 0 ? 1 : (float) totalTransaksiSP;

				float persentaseMenungguPersetujuanSP = ((float)totalMenungguPersetujuanSP * 100) / dividerSP;
				float persentaseDisetujuiSP = ((float)totalDisetujuiSP * 100) / dividerSP;
				float persentaseLunasSP = ((float)totalLunasSP * 100) / dividerSP;
				float persentaseDitolakSP = ((float)totalDitolakSP * 100) / dividerSP;
				float persentaseMenungguReversalSP = ((float)totalMenungguReversalSP * 100) / dividerSP;

				setoranPelunasan.put("totalMenungguPersetujuan", totalMenungguPersetujuanSP);
				setoranPelunasan.put("totalDisetujui", totalDisetujuiSP);
				setoranPelunasan.put("totalLunas", totalLunasSP);
				setoranPelunasan.put("totalDitolak", totalDitolakSP);
				setoranPelunasan.put("totalMenungguReversal", totalMenungguReversalSP);
				setoranPelunasan.put("totalTransaksi", totalTransaksiSP);
				setoranPelunasan.put("totalNominalMasuk", totalNominalMasukSP);
				setoranPelunasan.put("jumlahNasabah", jumlahNasabahSP);
				setoranPelunasan.put("persentaseMenungguPersetujuan", persentaseMenungguPersetujuanSP);
				setoranPelunasan.put("persentaseDisetujui", persentaseDisetujuiSP);
				setoranPelunasan.put("persentaseLunas", persentaseLunasSP);
				setoranPelunasan.put("persentaseDitolak", persentaseDitolakSP);
				setoranPelunasan.put("persentaseMenungguReversal", persentaseMenungguReversalSP);

				float dividerAll = dividerSA + dividerSP;

				long totalMenungguPersetujuanAll = totalMenungguPersetujuanSA + totalMenungguPersetujuanSP;
				long totalDisetujuiAll = totalDisetujuiSA + totalDisetujuiSP;
				long totalLunasAll = totalLunasSA + totalLunasSP;
				long totalDitolakAll = totalDitolakSA + totalDitolakSP;
				long totalMenungguReversalAll = totalMenungguReversalSA + totalMenungguReversalSP;

				float persentaseMenungguPersetujuanAll = ((float)totalMenungguPersetujuanAll * 100) / dividerAll;
				float persentaseDisetujuiAll = ((float)totalDisetujuiAll * 100) / dividerAll;
				float persentaseLunasAll = ((float)totalLunasAll * 100) / dividerAll;
				float persentaseDitolakAll = ((float)totalDitolakAll * 100) / dividerAll;
				float persentaseMenungguReversalAll = ((float)totalMenungguReversalAll * 100) / dividerAll;

				allProduct.put("totalMenungguPersetujuan", totalMenungguPersetujuanAll);
				allProduct.put("totalDisetujui", totalDisetujuiAll);
				allProduct.put("totalLunas", totalLunasAll);
				allProduct.put("totalDitolak", totalDitolakAll);
				allProduct.put("totalMenungguReversal", totalMenungguReversalAll);
				allProduct.put("totalTransaksi", totalTransaksiSA + totalTransaksiSP);
				allProduct.put("totalNominalMasuk", totalNominalMasukSA + totalNominalMasukSP);
				allProduct.put("jumlahNasabah", jumlahNasabahSA + jumlahNasabahSP);
				allProduct.put("persentaseMenungguPersetujuan", persentaseMenungguPersetujuanAll);
				allProduct.put("persentaseDisetujui", persentaseDisetujuiAll);
				allProduct.put("persentaseLunas", persentaseLunasAll);
				allProduct.put("persentaseDitolak", persentaseDitolakAll);
				allProduct.put("persentaseMenungguReversal", persentaseMenungguReversalAll);
			} else {
				long totalMenungguPersetujuanSA = setoranAwalRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(1), dateTglAwal, dateTglAkhir, kdCabang);
				long totalDisetujuiSA = setoranAwalRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(2), dateTglAwal, dateTglAkhir, kdCabang);
				long totalLunasSA = setoranAwalRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(3), dateTglAwal, dateTglAkhir, kdCabang);
				long totalDitolakSA = setoranAwalRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(4), dateTglAwal, dateTglAkhir, kdCabang);
				long totalMenungguReversalSA = setoranAwalRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(6), dateTglAwal, dateTglAkhir, kdCabang);
				long totalTransaksiSA = setoranAwalRepository.getCountBetweenDateWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang);
				Long totalNominalMasukSA = setoranAwalRepository.getNominalSetoranWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang) == null ? 0 : setoranAwalRepository.getNominalSetoranWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang);
				long jumlahNasabahSA = setoranAwalRepository.getCountBetweenDateWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang);;

				float dividerSA = totalTransaksiSA == 0 ? 1 : (float) totalTransaksiSA;

				float persentaseMenungguPersetujuanSA = ((float)totalMenungguPersetujuanSA * 100) / dividerSA;
				float persentaseDisetujuiSA = ((float)totalDisetujuiSA * 100) / dividerSA;
				float persentaseLunasSA = ((float)totalLunasSA * 100) / dividerSA;
				float persentaseDitolakSA = ((float)totalDitolakSA * 100) / dividerSA;
				float persentaseMenungguReversalSA = ((float)totalMenungguReversalSA * 100) / dividerSA;

				setoranAwal.put("totalMenungguPersetujuan", totalMenungguPersetujuanSA);
				setoranAwal.put("totalDisetujui", totalDisetujuiSA);
				setoranAwal.put("totalLunas", totalLunasSA);
				setoranAwal.put("totalDitolak", totalDitolakSA);
				setoranAwal.put("totalMenungguReversal", totalMenungguReversalSA);
				setoranAwal.put("totalTransaksi", totalTransaksiSA);
				setoranAwal.put("totalNominalMasuk", totalNominalMasukSA);
				setoranAwal.put("jumlahNasabah", jumlahNasabahSA);
				setoranAwal.put("persentaseMenungguPersetujuan", persentaseMenungguPersetujuanSA);
				setoranAwal.put("persentaseDisetujui", persentaseDisetujuiSA);
				setoranAwal.put("persentaseLunas", persentaseLunasSA);
				setoranAwal.put("persentaseDitolak", persentaseDitolakSA);
				setoranAwal.put("persentaseMenungguReversal", persentaseMenungguReversalSA);

				long totalMenungguPersetujuanSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(1), dateTglAwal, dateTglAkhir, kdCabang);
				long totalDisetujuiSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(2), dateTglAwal, dateTglAkhir, kdCabang);
				long totalLunasSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(3), dateTglAwal, dateTglAkhir, kdCabang);
				long totalDitolakSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(4), dateTglAwal, dateTglAkhir, kdCabang);
				long totalMenungguReversalSP = setoranPelunasanRepository.getCountByStatusTransaksiAndDateWithBranchCode(new StatusTransaksi(6), dateTglAwal, dateTglAkhir, kdCabang);
				long totalTransaksiSP = setoranPelunasanRepository.getCountBetweenDateWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang);;
				Long totalNominalMasukSP = setoranPelunasanRepository.getNominalSetoranWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang) == null ? 0 : setoranPelunasanRepository.getNominalSetoranWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang);
				long jumlahNasabahSP = setoranPelunasanRepository.getCountBetweenDateWithBranchCode(dateTglAwal, dateTglAkhir, kdCabang);;

				float dividerSP = totalTransaksiSP == 0 ? 1 : (float) totalTransaksiSP;

				float persentaseMenungguPersetujuanSP = ((float)totalMenungguPersetujuanSP * 100) / dividerSP;
				float persentaseDisetujuiSP = ((float)totalDisetujuiSP * 100) / dividerSP;
				float persentaseLunasSP = ((float)totalLunasSP * 100) / dividerSP;
				float persentaseDitolakSP = ((float)totalDitolakSP * 100) / dividerSP;
				float persentaseMenungguReversalSP = ((float)totalMenungguReversalSP * 100) / dividerSP;

				setoranPelunasan.put("totalMenungguPersetujuan", totalMenungguPersetujuanSP);
				setoranPelunasan.put("totalDisetujui", totalDisetujuiSP);
				setoranPelunasan.put("totalLunas", totalLunasSP);
				setoranPelunasan.put("totalDitolak", totalDitolakSP);
				setoranPelunasan.put("totalMenungguReversal", totalMenungguReversalSP);
				setoranPelunasan.put("totalTransaksi", totalTransaksiSP);
				setoranPelunasan.put("totalNominalMasuk", totalNominalMasukSP);
				setoranPelunasan.put("jumlahNasabah", jumlahNasabahSP);
				setoranPelunasan.put("persentaseMenungguPersetujuan", persentaseMenungguPersetujuanSP);
				setoranPelunasan.put("persentaseDisetujui", persentaseDisetujuiSP);
				setoranPelunasan.put("persentaseLunas", persentaseLunasSP);
				setoranPelunasan.put("persentaseDitolak", persentaseDitolakSP);
				setoranPelunasan.put("persentaseMenungguReversal", persentaseMenungguReversalSP);

				float dividerAll = dividerSA + dividerSP;

				long totalMenungguPersetujuanAll = totalMenungguPersetujuanSA + totalMenungguPersetujuanSP;
				long totalDisetujuiAll = totalDisetujuiSA + totalDisetujuiSP;
				long totalLunasAll = totalLunasSA + totalLunasSP;
				long totalDitolakAll = totalDitolakSA + totalDitolakSP;
				long totalMenungguReversalAll = totalMenungguReversalSA + totalMenungguReversalSP;

				float persentaseMenungguPersetujuanAll = ((float)totalMenungguPersetujuanAll * 100) / dividerAll;
				float persentaseDisetujuiAll = ((float)totalDisetujuiAll * 100) / dividerAll;
				float persentaseLunasAll = ((float)totalLunasAll * 100) / dividerAll;
				float persentaseDitolakAll = ((float)totalDitolakAll * 100) / dividerAll;
				float persentaseMenungguReversalAll = ((float)totalMenungguReversalAll * 100) / dividerAll;

				allProduct.put("totalMenungguPersetujuan", totalMenungguPersetujuanAll);
				allProduct.put("totalDisetujui", totalDisetujuiAll);
				allProduct.put("totalLunas", totalLunasAll);
				allProduct.put("totalDitolak", totalDitolakAll);
				allProduct.put("totalMenungguReversal", totalMenungguReversalAll);
				allProduct.put("totalTransaksi", totalTransaksiSA + totalTransaksiSP);
				allProduct.put("totalNominalMasuk", totalNominalMasukSA + totalNominalMasukSP);
				allProduct.put("jumlahNasabah", jumlahNasabahSA + jumlahNasabahSP);
				allProduct.put("persentaseMenungguPersetujuan", persentaseMenungguPersetujuanAll);
				allProduct.put("persentaseDisetujui", persentaseDisetujuiAll);
				allProduct.put("persentaseLunas", persentaseLunasAll);
				allProduct.put("persentaseDitolak", persentaseDitolakAll);
				allProduct.put("persentaseMenungguReversal", persentaseMenungguReversalAll);
			}
			result.add(setoranAwal);
			result.add(setoranPelunasan);
			result.add(allProduct);
			return new Response("00", result, "BERHASIL");
		} catch (Exception e) {
			e.printStackTrace();
			return new Response("99", null, e.getLocalizedMessage());
		}
	}
}
