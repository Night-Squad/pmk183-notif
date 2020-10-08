package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.SetoranPelunasan;
import com.bjbs.haji.business.repositories.haji.SetoranAwalRepository;
import com.bjbs.haji.business.repositories.haji.SetoranPelunasanRepository;
import com.io.iona.springboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/rtjh")
public class RtjhController {

    @Autowired
    private StorageService storageService;

    @Autowired
    SetoranAwalRepository setoranAwalRepository;

    @Autowired
    SetoranPelunasanRepository setoranPelunasanRepository;

    @PostMapping("/generate")
    public Object generate(@RequestBody MultipartFile file) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            String st;
            while ((st = bufferedReader.readLine()) != null) {
                Map<String, Object> item = new HashMap<>();
                String[] row = st.split("\\t");
                item.put("branchCode", row[0]);
                item.put("noRekening", row[1]);
                item.put("namaJemaah", row[2]);
                item.put("tglTutup", row[3]);
                SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoRekening(row[1]);
                if (setoranAwal != null) {
                    item.put("noValidasi", setoranAwal.getNoValidasi());
                    item.put("noVa", setoranAwal.getVirtualAccount());
                } else {
                    item.put("noValidasi", "-");
                    item.put("noVa", "-");
                }
                SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.getSetoranPelunasanByNoRekening(row[1]);
                if (setoranPelunasan != null) {
                    item.put("noPorsi", setoranPelunasan.getNoPorsi());
                } else {
                    item.put("noPorsi", "-");
                }
                result.add(item);
            }
            result.remove(0);
            return new Response("00",result, "Berhasil");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return new Response("99",result, ex.getLocalizedMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            return new Response("99",result, ex.getLocalizedMessage());
        }
    }

    @PostMapping("/export")
    public void export(@RequestBody MultipartFile file, @RequestParam("tglData") String tglData, HttpServletResponse response) throws ParseException {
        List<Map<String, Object>> result = new ArrayList<>();
        Date date = new SimpleDateFormat("yyyy/MM/dd").parse(tglData);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            OutputStream outputStream = response.getOutputStream();
            Writer outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write("KODE_BANK|NAMA_BANK|TGL_DATA\n");
            outputStreamWriter.write("425|BANK JABAR BANTEN SYARIAH|" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "\n");
            outputStreamWriter.write("NO_VALIDASI|NO_PORSI|NO_VA|NAMA_JEMAAH|NO_REKENING|TGL_TUTUP\n");

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=RK_425_" + new SimpleDateFormat("yyyyMMdd").format(date) + ".txt");

            String st;
            int index = 0;
            while ((st = bufferedReader.readLine()) != null) {
                index++;
                if (index == 1) {
                    continue;
                }
                StringBuilder item = new StringBuilder();
                String[] row = st.split("\\t");
                SetoranAwal setoranAwal = setoranAwalRepository.getSetoranAwalByNoRekening(row[1]);
                if (setoranAwal != null) {
                    item.append(setoranAwal.getNoValidasi());
                }
                item.append("|");
                SetoranPelunasan setoranPelunasan = setoranPelunasanRepository.getSetoranPelunasanByNoRekening(row[1]);
                if (setoranPelunasan != null) {
                    item.append(setoranPelunasan.getNoPorsi());
                }
                item.append("|");
                if (setoranAwal != null) {
                    item.append(setoranAwal.getVirtualAccount());
                }
                item.append("|");
                item.append(row[2]);
                item.append("|");
                item.append(row[1]);
                item.append("|");
                item.append(row[3]);
                item.append("\n");
                outputStreamWriter.write(item.toString());
            }
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
