package com.pmk.notif.repositories.pubsubs;

import com.pmk.notif.models.pubsubs.MasterApiNotif;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Date;

public interface MasterApiNotifRepository extends JpaRepository<MasterApiNotif, Long> {

    Page<MasterApiNotif> findBySentOrSentAndReceived(Boolean sent, Boolean sentOr, Boolean received, Pageable pageable);
    Page<MasterApiNotif> findByCreatedAtBetweenAndSentOrSentAndReceived(Date startDate, Date endDate, Boolean sent, Boolean sentOr, Boolean received, Pageable pageable);
    Page<MasterApiNotif> findByVaAccNoContainingAndCreatedAtBetweenAndSentOrSentAndReceived(String vaAccNo, Date startDate, Date endDate, Boolean sent, Boolean sentOr, Boolean received, Pageable pageable);
    Page<MasterApiNotif> findByTxAmountAndCreatedAtBetweenAndSentOrSentAndReceived(Long txAmount, Date startDate, Date endDate, Boolean sent, Boolean sentOr, Boolean received, Pageable pageable);
    Page<MasterApiNotif> findByTxReferenceNoContainingAndCreatedAtBetweenAndSentOrSentAndReceived(String txReferenceNo, Date startDate, Date endDate, Boolean sent, Boolean sentOr, Boolean received, Pageable pageable);
    Page<MasterApiNotif> findByCompanyIdAndCreatedAtBetweenAndSentOrSentAndReceived(Integer companyId, Date startDate, Date endDate, Boolean sent, Boolean sentOr, Boolean received, Pageable pageable);

    Page<MasterApiNotif> findBySentTrueAndReceivedTrue(Pageable pageable);
    Page<MasterApiNotif> findByCreatedAtBetweenAndSentTrueAndReceivedTrue(Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByVaAccNoContainingAndCreatedAtBetweenAndSentTrueAndReceivedTrue(String vaAccNo, Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByTxAmountAndCreatedAtBetweenAndSentTrueAndReceivedTrue(Long txAmount, Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByTxReferenceNoContainingAndCreatedAtBetweenAndSentTrueAndReceivedTrue(String txReferenceNo, Date startDate, Date endDate, Pageable pageable);
    Page<MasterApiNotif> findByCompanyIdAndCreatedAtBetweenAndSentTrueAndReceivedTrue(Integer companyId, Date startDate, Date endDate, Pageable pageable);
}
