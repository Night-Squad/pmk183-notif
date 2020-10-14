package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.StatusTransaksi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusTransaksiRepository extends JpaRepository<StatusTransaksi, Long> {

    
    
}