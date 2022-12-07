package com.hanwha.tax.batch.fingerauto.service;

import com.hanwha.tax.batch.entity.finger.MailHistory;
import com.hanwha.tax.batch.entity.finger.MailReceiver;
import com.hanwha.tax.batch.fingerauto.repository.MailHistoryRepository;
import com.hanwha.tax.batch.fingerauto.repository.MailReceiverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("fingerAutoService")
public class FingerAutoService {

    @Autowired
    private MailHistoryRepository mailHistoryRepository;

    @Autowired
    private MailReceiverRepository mailReceiverRepository;

    public void sendMail() {
        String subject = "";
        String content = "";
        String email = "";
        String name = "";
        String ip = "";

        MailHistory mailHistory = new MailHistory(subject, content, email);
        mailHistory = mailHistoryRepository.save(mailHistory);
        MailReceiver mailReceiver = new MailReceiver(mailHistory.getMailNo(), name, email, ip);
        mailReceiverRepository.save(mailReceiver);
    }
}
