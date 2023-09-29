package com.pmk.notif.repositories.pubsubs;

import com.pmk.notif.models.pubsubs.MasterApiNotif;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;

public interface MasterApiNotifRepository extends JpaRepository<MasterApiNotif, Long> {

    @Query(value = "SELECT * FROM master_api_notif WHERE (sent IS true OR sent IS NULL) AND received IS NULL", nativeQuery = true)
    Page<MasterApiNotif> findBySentAndReceived( Pageable pageable);
    @Query(value = "SELECT * FROM master_api_notif WHERE (trx_time BETWEEN :startDate AND :endDate ) AND (sent IS true OR sent IS NULL) AND received IS NULL", nativeQuery = true)
    Page<MasterApiNotif> findByTrxTimeBetweenAndSentAndReceived(Date startDate, Date endDate,  Pageable pageable);
    @Query(value = "SELECT * FROM master_api_notif WHERE va_acc_no LIKE %:vaAccNo% AND (trx_time BETWEEN :startDate AND :endDate) AND (sent IS true OR sent IS NULL) AND received IS NULL", nativeQuery = true)
    Page<MasterApiNotif> findByVaAccNoContainingAndTrxTimeBetweenAndSentAndReceived(String vaAccNo, Date startDate, Date endDate,  Pageable pageable);
    @Query(value = "SELECT * FROM master_api_notif WHERE tx_amount=:txAmount AND (trx_time BETWEEN :startDate AND :endDate ) AND (sent IS true OR sent IS NULL) AND received IS NULL", nativeQuery = true)
    Page<MasterApiNotif> findByTxAmountAndTrxTimeBetweenAndSentAndReceived(Long txAmount, Date startDate, Date endDate,  Pageable pageable);
    @Query(value = "SELECT * FROM master_api_notif WHERE tx_reference LIKE %:txReferenceNo% AND (trx_time BETWEEN :startDate AND :endDate ) AND (sent IS true OR sent IS NULL) AND received IS NULL", nativeQuery = true)
    Page<MasterApiNotif> findByTxReferenceNoContainingAndTrxTimeBetweenAndSentAndReceived(String txReferenceNo, Date startDate, Date endDate,  Pageable pageable);
    @Query(value = "SELECT * FROM master_api_notif WHERE company_id=:companyId AND (trx_time BETWEEN :startDate AND :endDate ) AND (sent IS true OR sent IS NULL) AND received IS NULL", nativeQuery = true)
    Page<MasterApiNotif> findByCompanyIdAndTrxTimeBetweenAndSentAndReceived(Integer companyId, Date startDate, Date endDate,  Pageable pageable);

    Page<MasterApiNotif> findBySentTrueAndReceivedTrue(Pageable pageable);
    Page<MasterApiNotif> findByTrxTimeBetweenAndSentTrueAndReceivedTrue(Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByVaAccNoContainingAndTrxTimeBetweenAndSentTrueAndReceivedTrue(String vaAccNo, Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByTxAmountAndTrxTimeBetweenAndSentTrueAndReceivedTrue(Long txAmount, Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByTxReferenceNoContainingAndTrxTimeBetweenAndSentTrueAndReceivedTrue(String txReferenceNo, Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByCompanyIdAndTrxTimeBetweenAndSentTrueAndReceivedTrue(Integer companyId, Date startDate, Date endDate, Pageable pageable);
}
