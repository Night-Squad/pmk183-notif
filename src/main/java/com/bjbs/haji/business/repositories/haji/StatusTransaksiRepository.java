package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.StatusTransaksi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusTransaksiRepository extends JpaRepository<StatusTransaksi, Long> {
	
	@Query(value = "select * from status_transaksi where status_active =true",nativeQuery = true)
	List<StatusTransaksi> findAllStatusTransaksi(); 
    
}