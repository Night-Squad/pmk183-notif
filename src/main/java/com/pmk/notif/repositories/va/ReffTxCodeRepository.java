package com.pmk.notif.repositories.va;

import com.pmk.notif.models.va.ReffTxCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReffTxCodeRepository extends JpaRepository<ReffTxCode, Short> {

    Optional<ReffTxCode> findFirstByTrnCode(String trnCode);
}
