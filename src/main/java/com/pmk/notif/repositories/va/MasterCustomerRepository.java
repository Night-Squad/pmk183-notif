package com.pmk.notif.repositories.va;


import com.pmk.notif.models.va.MasterCustomer;
import com.pmk.notif.models.va.MasterCustomerPKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterCustomerRepository extends JpaRepository<MasterCustomer, MasterCustomerPKey> {

    @Query(value = "SELECT * FROM master_customer WHERE va_acc_no = :vaAccNo AND bit_id = :bitId", nativeQuery = true)
    Optional<MasterCustomer> findByVaAccNoAndBitId(String vaAccNo, Short bitId);
}
