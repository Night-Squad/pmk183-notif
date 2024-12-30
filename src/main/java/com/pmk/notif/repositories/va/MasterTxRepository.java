package com.pmk.notif.repositories.va;

import com.pmk.notif.models.va.MasterTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterTxRepository extends JpaRepository<MasterTx, Integer> {

    @Query(value = "SELECT * FROM master_tx WHERE archive_no = :tx_reference_no AND (is_reversal <> true OR is_reversal IS NULL) and created_at BETWEEN :start_date AND :end_date", nativeQuery = true)
    Optional<MasterTx> findMasterTxBetween(String tx_reference_no, String start_date, String end_date);
}
