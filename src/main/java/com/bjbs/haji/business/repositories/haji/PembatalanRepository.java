package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.Pembatalan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PembatalanRepository extends JpaRepository<Pembatalan, Long> {
}
