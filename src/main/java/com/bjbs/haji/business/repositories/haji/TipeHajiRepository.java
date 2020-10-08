package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.TipeHaji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipeHajiRepository extends JpaRepository<TipeHaji, Long> {

    @Query(value = "SELECT tp from TipeHaji tp WHERE tp.kodeHaji=:kodeHaji")
    TipeHaji getTipeHajiByCode(String kodeHaji);

}
