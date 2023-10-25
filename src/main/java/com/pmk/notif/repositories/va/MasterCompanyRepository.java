package com.pmk.notif.repositories.va;

import com.pmk.notif.models.va.MasterCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MasterCompanyRepository extends JpaRepository<MasterCompany, Integer> {

    @Query(value = "SELECT id, company_name FROM master_company WHERE id = :id", nativeQuery = true)
    Optional<Object> findByCompanyId(Integer id);

    Optional<MasterCompany> findFirstByKdComp(Integer kdComp);

}
