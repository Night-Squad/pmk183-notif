package com.pmk.notif.repositories.pubsubs;


import com.pmk.notif.models.pubsubs.MasterReversalNotif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MasterReversalNotifRepository extends JpaRepository<MasterReversalNotif, Long> {

    @Query(value = "SELECT * FROM master_reversal_notif WHERE tx_reference_no = :tx_reference_no AND created_at BETWEEN :start_date AND :end_date ORDER BY created_at DESC, id DESC", nativeQuery = true)
    List<MasterReversalNotif> findMasterReversalNotif(String tx_reference_no, LocalDateTime start_date, LocalDateTime end_date);
}
