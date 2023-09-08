package com.bjbs.haji.business.repositories.haji;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.bjbs.haji.business.models.SetoranAwal;

import com.bjbs.haji.business.models.StatusTransaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SetoranAwalRepository extends JpaRepository<SetoranAwal, Long> {

    @Query("SELECT sa FROM SetoranAwal sa  where sa.noRekening=:noRekening")
    SetoranAwal getSetoranAwalByNoRekening(String noRekening);

    @Query("SELECT sa FROM SetoranAwal sa  where sa.noValidasi=:noValidasi")
    SetoranAwal getSetoranAwalByNoValidasi(String noValidasi);

    @Query("SELECT sa FROM SetoranAwal sa WHERE sa.tanggalTransaksi BETWEEN :tglAwal AND :tglAkhir ORDER BY sa.tanggalTransaksi")
    List<SetoranAwal> getListSetoranAwalPeriod(Date tglAwal, Date tglAkhir);

    @Query("SELECT sa FROM SetoranAwal sa where sa.transactionId=:transactionId")
    SetoranAwal getSetoranAwalByTransactionId(String transactionId);

    @Query("SELECT count(sa) FROM SetoranAwal sa where sa.statusTransaksi=:statusTransaksi and sa.createdDate BETWEEN :startDate AND :endDate")
    Long getCountByStatusTransaksiAndDate(StatusTransaksi statusTransaksi, Date startDate, Date endDate);

    @Query(value = "SELECT count(sa) FROM setoran_awal sa inner join branch b on sa.branch_code = b.branch_code where sa.status_transaksi_id=:statusTransaksi and sa.created_date BETWEEN :startDate AND :endDate and b.is_bjbs =:isBjbs", nativeQuery = true)
    Long getCountByStatusTransaksiAndDate(@Param("statusTransaksi") int statusTransaksi, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("isBjbs") Boolean isBjbs);


    @Query("SELECT count(sa) FROM SetoranAwal sa where sa.statusTransaksi=:statusTransaksi and sa.createdDate BETWEEN :startDate AND :endDate AND sa.branchCode=:branchCode")
    Long getCountByStatusTransaksiAndDateWithBranchCode(StatusTransaksi statusTransaksi, Date startDate, Date endDate, String branchCode);

    @Query("SELECT count(sa) FROM SetoranAwal sa WHERE CONCAT(sa.createdDate, '') LIKE CONCAT(:date, '%')")
    public Long getCountByDate(@Param("date") String date);

    @Query("SELECT count(sa) FROM SetoranAwal sa WHERE CONCAT(sa.createdDate, '') LIKE CONCAT(:date, '%') and sa.branchCode =:branchCode")
    public Long getCountByDate(@Param("date") String date, @Param("branchCode") String branchCode);

    @Query(value = "SELECT count(sa) FROM setoran_awal sa inner join branch b on sa.branch_code = b.branch_code WHERE CONCAT(sa.created_date, '') LIKE CONCAT(:date, '%') and b.is_bjbs =:isBjbs", nativeQuery = true)
    public Long getCountByDate(@Param("date") String date, @Param("isBjbs") Boolean isBjbs);

    @Query("SELECT count(sa) FROM SetoranAwal sa WHERE sa.createdDate BETWEEN :startDate AND :endDate")
    public Long getCountBetweenDate(Date startDate, Date endDate);

    @Query(value = "SELECT count(sa) FROM setoran_awal sa inner join branch b on sa.branch_code = b.branch_code WHERE sa.created_date BETWEEN :startDate AND :endDate and b.is_bjbs =:isBjbs", nativeQuery = true)
    public Long getCountBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("isBjbs") Boolean isBjbs);

    @Query("SELECT count(sa) FROM SetoranAwal sa WHERE sa.createdDate BETWEEN :startDate AND :endDate AND sa.branchCode=:branchCode")
    public Long getCountBetweenDateWithBranchCode(Date startDate, Date endDate, String branchCode);

    @Query("SELECT sum(sa.nominalSetoran) FROM SetoranAwal sa WHERE sa.tanggalTransaksi BETWEEN :tglAwal AND :tglAkhir")
    Long getNominalSetoran(Date tglAwal, Date tglAkhir);

    @Query( value = "SELECT sum(sa.nominal_setoran) FROM setoran_awal sa inner join branch b on sa.branch_code = b.branch_code WHERE sa.tanggal_transaksi BETWEEN :tglAwal AND :tglAkhir and b.is_bjbs =:isBjbs", nativeQuery = true)
    Long getNominalSetoran(@Param("tglAwal") Date tglAwal, @Param("tglAkhir") Date tglAkhir, @Param("isBjbs") Boolean isBjbs);

    @Query("SELECT sum(sa.nominalSetoran) FROM SetoranAwal sa WHERE sa.tanggalTransaksi BETWEEN :tglAwal AND :tglAkhir AND sa.branchCode=:branchCode")
    Long getNominalSetoranWithBranchCode(Date tglAwal, Date tglAkhir, String branchCode);

    @Query("FROM SetoranAwal WHERE isUploaded = false")
    List<SetoranAwal> getSetoranAwalNotUploaded();

    // @Query(value = "update setoran_awal set status_transaksi_id = 7 where setoran_awal_id = ?1",nativeQuery = true)
    // Map<String,Object> updateDataStatusId (Long setoranAwalId);
}