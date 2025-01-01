package com.pmk.notif.repositories.va;

import com.pmk.notif.models.va.MasterTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MasterTxRepository extends JpaRepository<MasterTx, Integer> {

    @Query(value = "SELECT * FROM master_tx WHERE archive_no = :tx_reference_no AND (is_reversal <> true OR is_reversal IS NULL) and created_at BETWEEN :start_date AND :end_date", nativeQuery = true)
    List<MasterTx> findMasterTxBetween(String tx_reference_no, LocalDateTime start_date, LocalDateTime end_date);
}
