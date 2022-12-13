package com.hanwha.tax.batch.fingerauto.mail.repository;

import com.hanwha.tax.batch.fingerauto.entity.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailHistoryRepository extends JpaRepository<MailHistory, Long> {
}