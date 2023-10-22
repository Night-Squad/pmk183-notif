package com.pmk.notif.repositories.va;

import com.pmk.notif.models.va.MasterTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterTxRepository extends JpaRepository<MasterTx, Integer> {
}
