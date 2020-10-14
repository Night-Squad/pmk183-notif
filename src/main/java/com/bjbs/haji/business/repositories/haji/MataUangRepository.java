package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.MataUang;
import com.bjbs.haji.business.models.TipeHaji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MataUangRepository extends JpaRepository<MataUang, Long> {

    @Query(value = "SELECT mt from MataUang mt WHERE mt.kodeMataUang=:kodeMataUang")
    MataUang getMataUangByCode(String kodeMataUang);
}
