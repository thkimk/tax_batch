package com.hanwha.tax.batch.fingerauto.repository;

import com.hanwha.tax.batch.entity.finger.MailReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailReceiverRepository extends JpaRepository<MailReceiver, Long> {
}