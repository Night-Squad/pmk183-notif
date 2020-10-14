package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.models.SettingTbl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingTblRepository extends JpaRepository<SettingTbl, Long> {

    @Query(value = "select * from setting_tbl st where st.setting_code = ?1", nativeQuery = true)
    SettingTbl getSettingBySettingCode(String settingCode);

}
