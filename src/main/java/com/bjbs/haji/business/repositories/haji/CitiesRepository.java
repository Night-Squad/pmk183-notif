package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.Cities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CitiesRepository extends JpaRepository<Cities, Long> {

    @Query("SELECT city FROM Cities city WHERE cityCodeCbs=:cityCodeCbs")
    Cities findByCityCodeCbs(String cityCodeCbs);
}
