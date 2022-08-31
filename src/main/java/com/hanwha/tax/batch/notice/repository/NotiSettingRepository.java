package com.hanwha.tax.batch.notice.repository;

import com.hanwha.tax.batch.entity.NotiSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotiSettingRepository extends JpaRepository<NotiSetting, String> {
    /**
     * 고객번호 기준으로 알람설정 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from noti_setting ns where ns.cust_id=:custId", nativeQuery = true)
    void deleteById(String custId);
}
