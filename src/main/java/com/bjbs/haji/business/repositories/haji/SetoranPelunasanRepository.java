package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.SetoranAwal;
import com.bjbs.haji.business.models.SetoranPelunasan;

import com.bjbs.haji.business.models.StatusTransaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SetoranPelunasanRepository extends JpaRepository<SetoranPelunasan, Long> {

    @Query("SELECT sp FROM SetoranPelunasan sp  where sp.noRekening=:noRekening")
    SetoranPelunasan getSetoranPelunasanByNoRekening(String noRekening);

    @Query("SELECT sa FROM SetoranPelunasan sa  where sa.noPorsi=:noPorsi")
    SetoranPelunasan getSetoranPelunasanByNoPorsi(String noPorsi);

    @Query("SELECT sa FROM SetoranPelunasan sa WHERE sa.tanggalTransaksi BETWEEN :tglAwal AND :tglAkhir ORDER BY tanggalTransaksi")
    List<SetoranPelunasan> getListSetoranPelunasanPeriod(Date tglAwal, Date tglAkhir);

    @Query("SELECT sa FROM SetoranPelunasan sa where sa.transactionId=:transactionId")
    SetoranPelunasan getSetoranPelunasanByTransactionId(String transactionId);

    @Query("SELECT count(sp) FROM SetoranPelunasan sp where sp.statusTransaksi=:statusTransaksi and sp.createdDate BETWEEN :startDate AND :endDate")
    Long getCountByStatusTransaksiAndDate(StatusTransaksi statusTransaksi, Date startDate, Date endDate);

    @Query("SELECT count(sp) FROM SetoranPelunasan sp where sp.statusTransaksi=:statusTransaksi and sp.createdDate BETWEEN :startDate AND :endDate AND sp.branchCode=:branchCode")
    Long getCountByStatusTransaksiAndDateWithBranchCode(StatusTransaksi statusTransaksi, Date startDate, Date endDate, String branchCode);

    @Query("SELECT count(sp) FROM SetoranPelunasan sp WHERE CONCAT(sp.createdDate, '') LIKE CONCAT(:date, '%')")
    public Long getCountByDate(@Param("date") String date);

    @Query("SELECT count(sp) FROM SetoranPelunasan sp WHERE sp.createdDate BETWEEN :startDate AND :endDate")
    public Long getCountBetweenDate(Date startDate, Date endDate);

    @Query("SELECT count(sp) FROM SetoranPelunasan sp WHERE sp.createdDate BETWEEN :startDate AND :endDate AND sp.branchCode=:branchCode")
    public Long getCountBetweenDateWithBranchCode(Date startDate, Date endDate, String branchCode);

    @Query("SELECT sum(sp.nominalSetoran) FROM SetoranPelunasan sp WHERE sp.tanggalTransaksi BETWEEN :tglAwal AND :tglAkhir")
    Long getNominalSetoran(Date tglAwal, Date tglAkhir);

    @Query("SELECT sum(sp.nominalSetoran) FROM SetoranPelunasan sp WHERE sp.tanggalTransaksi BETWEEN :tglAwal AND :tglAkhir AND sp.branchCode=:branchCode")
    Long getNominalSetoranWithBranchCode(Date tglAwal, Date tglAkhir, String branchCode);
}