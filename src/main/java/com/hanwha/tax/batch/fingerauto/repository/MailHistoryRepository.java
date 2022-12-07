package com.hanwha.tax.batch.fingerauto.repository;

import com.hanwha.tax.batch.entity.finger.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailHistoryRepository extends JpaRepository<MailHistory, Long> {
}