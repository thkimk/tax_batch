package com.hanwha.tax.batch.mydata.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.MydataIncome;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.mydata.model.AbstractMydataCoocon;
import com.hanwha.tax.batch.mydata.model.BankTrans;
import com.hanwha.tax.batch.mydata.model.CardAppr;
import com.hanwha.tax.batch.mydata.repository.MydataIncomeRepository;
import com.hanwha.tax.batch.mydata.repository.MydataOutgoingRepository;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.hanwha.tax.batch.Constants.*;

@Slf4j
@Service("mydataService")
public class MydataService {

    @Autowired
    MydataIncomeRepository mydataIncomeRepository;

    @Autowired
    MydataOutgoingRepository mydataOutgoingRepository;

    @Autowired
    AuthService authService;

    @Autowired
    CustService custService;

    @Value("${tax.sftp.user}")
    private String mydataSftpUser;

    @Value("${tax.sftp.pwd}")
    private String mydataSftpPwd;

    @Value("${tax.sftp.host}")
    private String mydataSftpHost;

    @Value("${tax.sftp.port}")
    private int mydataSftpPort;


    public void batchDataJob() {
        String yesterday = Utils.getYesterday(); // 어제일자
        String down_path = COOCON_FILE_DOWNLOAD_PATH;
        String mydata_path = COOCON_FILE_MYDATA_PATH + yesterday + "/";
        String bankFile = yesterday + "_" + mydataSftpUser + "_" + AbstractMydataCoocon.FILE_KIND.쿠콘.getCode() + "_" + BANK_TRANS_FILE;
        String cardFile = yesterday + "_" + mydataSftpUser + "_" + AbstractMydataCoocon.FILE_KIND.쿠콘.getCode() + "_" + CARD_APPR_FILE;

        // SFTP Get 수행 (/nas/tax/down)
        log.info("▶︎▶︎▶︎ SFTP 파일 다운로드 시작");
        mydataSftpGet(down_path);

        // zip 압축 해제 (/nas/tax/mydata/yyyymmdd/*)
        log.info("▶︎▶︎▶︎ BATCH 파일 압축 풀기 시작");
        mydataUnzip(down_path + bankFile + ".zip", mydata_path);
        mydataUnzip(down_path + cardFile + ".zip", mydata_path);

        // File load (parsing)
        // DB Upsert (mydata_income)
        log.info("▶︎▶︎▶︎ BANK_TRANS 파일 저장 시작");
        mydataIncomeLoad(mydata_path + bankFile);
        log.info("▶︎▶︎▶︎ CARD_APPR 파일 저장 시작");
        mydataOutgoingLoad(mydata_path + cardFile);

    }

    private void mydataSftpGet(String downPath) {
        String yesterday = Utils.getYesterday(); //yyyymmdd = "20220705";//kkk
        String sftp_path = "/data/" + yesterday + "/";
        String bankFile = yesterday + "_" + mydataSftpUser + "_" + AbstractMydataCoocon.FILE_KIND.쿠콘.getCode() + "_" + BANK_TRANS_FILE + ".zip";
        String cardFile = yesterday + "_" + mydataSftpUser + "_" + AbstractMydataCoocon.FILE_KIND.쿠콘.getCode() + "_" + CARD_APPR_FILE + ".zip";
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        // sftp 세션 생성
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(mydataSftpUser, mydataSftpHost, mydataSftpPort);
            session.setPassword(mydataSftpPwd);
            session.setConfig(config);
            session.connect(3000);
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // down bank
            FileOutputStream fo1 = new FileOutputStream(new File(downPath + bankFile));
            channelSftp.get(sftp_path + bankFile, fo1);

            // down card
            FileOutputStream fo2 = new FileOutputStream(new File(downPath + cardFile));
            channelSftp.get(sftp_path + cardFile, fo2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channelSftp.disconnect();
            session.disconnect();
        }
    }

    private void checkPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private void mydataUnzip(String source_file, String target_path) {
        File zipFile = new File(source_file);
        checkPath(target_path);

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile))) {
            try (ZipInputStream zipInputStream = new ZipInputStream(in)) {
                ZipEntry zipEntry = null;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    int len = 0;
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target_path + zipEntry.getName()))) {
                        while ((len = zipInputStream.read()) != -1) {
                            out.write(len);
                        }

                        zipInputStream.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AbstractMydataCoocon getMydataObjByName(String model_name) {
        if (Utils.isEmpty(model_name))
            return null;

        model_name = Utils.convertToCamelCase("_"+model_name);

        AbstractMydataCoocon info = null;
        try {
            Class<?> c = Class.forName("com.hanwha.tax.batch.mydata.model." + model_name);
            info = (AbstractMydataCoocon) c.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return info;
    }

    /**
     * header 데이터 검증
     * @param row_header
     * @return
     */
    private boolean validateHeader(String row_header) {
        if (Utils.isEmpty(row_header)) {
            log.error("헤더 데이터를 확인해 주시기 바랍니다.");
            return false;
        }

        String[] columnArr = row_header.split("|");

        if (columnArr.length != 5) {
            log.error("헤더 데이터 수가 일치하지 않습니다.");
            return false;
        }

        if (AbstractMydataCoocon.ROW_TYPE.헤더레코드부.getCode().equals(columnArr[0])) {
            log.error("헤더의 식별코드를 확인해 주시기 바랍니다.");
            return false;
        }

        if ("A0000001".equals(columnArr[2])) {
            log.error("헤더의 기관코드를 확인해 주시기 바랍니다.");
            return false;
        }

        return true;
    }

    /**
     * 마이데이터 수입정보 저장
     * @param mydataIncome
     * @return
     */
    private MydataIncome saveMydataIncome(MydataIncome mydataIncome) {
        List<MydataIncome> listMydataIncome = mydataIncomeRepository.findByDataPk(mydataIncome);

        if (1 < listMydataIncome.size()) {
            log.error("마이데이터 수입정보에 중복 값이 존재합니다.");
            return null;
        }

        mydataIncome.setId(listMydataIncome.get(0).getId());
        mydataIncome.setCreateDt(listMydataIncome.get(0).getCreateDt());

        return mydataIncomeRepository.save(mydataIncome);
    }

    /**
     * 쿠콘 마이데이터 은행(수입) 파일 DB save
     * @param source_file
     */
    private void mydataIncomeLoad(String source_file) {
        // 마이데이터 은행(수입) 클래스 생성
        BankTrans bankTrans = (BankTrans) getMydataObjByName(BANK_TRANS_FILE);
        if (bankTrans == null) {
            log.error("쿠콘 마이데이터 은행(수입) 클래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 마이데이터 파일 읽기
        try {
            BufferedReader reader = new BufferedReader(new FileReader(source_file));

            String str;
            boolean isIng = false;
            while ((str = reader.readLine()) != null) {
                log.debug("## mydataIncomeLoad : {}", str);
                String[] vals = str.split("|");
                if (vals == null) break;
                if (vals[0].equals("ST")) {
                    isIng = true;

                    // 헤더 검증
                    if (!validateHeader(str))
                        return;
                } else if (vals[0].equals("ED")) {
                    break;
                } else {
                    // 본처리
                    if (isIng) {
                        if (str == null) break;

                        // 마이데이터 은행(수입) 전문 파싱
                        bankTrans.parseData(str);

                        // 고객정보 확인
                        String custId = authService.getCustIdByCi(bankTrans.getCI());

                        // 마이데이터 수입 테이블 저장
                        saveMydataIncome(new MydataIncome().convertByBankTrans(custId, bankTrans));

                        // 고객정보상세 자산변경일시 업데이트
                        custService.updateCustMydataDt(custId);

                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 마이데이터 경비정보 저장
     * @param mydataOutgoing
     * @return
     */
    private MydataOutgoing saveMydataOutgoing(MydataOutgoing mydataOutgoing) {
        List<MydataOutgoing> listMydataOutgoing = mydataOutgoingRepository.findByDataPk(mydataOutgoing);

        if (1 <listMydataOutgoing.size()) {
            log.error("마이데이터 경비정보에 중복 값이 존재합니다.");
            return null;
        }

        mydataOutgoing.setId(listMydataOutgoing.get(0).getId());
        mydataOutgoing.setCreateDt(listMydataOutgoing.get(0).getCreateDt());

        return mydataOutgoingRepository.save(mydataOutgoing);
    }

    /**
     * 쿠콘 마이데이터 카드(경비) 파일 DB save
     * @param source_file
     */
    private void mydataOutgoingLoad(String source_file) {
        // 마이데이터 카드(경비) 클래스 생성
        CardAppr cardAppr = (CardAppr) getMydataObjByName(CARD_APPR_FILE);
        if (cardAppr == null) {
            log.error("쿠콘 마이데이터 카드(경비) 클래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 마이데이터 파일 읽기
        try {
            BufferedReader reader = new BufferedReader(new FileReader(source_file));

            String str;
            boolean isIng = false;
            while ((str = reader.readLine()) != null) {
                log.debug("## mydataOutgoingLoad : {}", str);
                String[] vals = str.split("|");
                if (vals == null) break;
                if (vals[0].equals("ST")) {
                    isIng = true;
                } else if (vals[0].equals("ED")) {
                    break;
                } else {
                    // 본처리
                    if (isIng) {
                        if (str == null) break;

                        // 마이데이터 카드(경비) 전문 파싱
                        cardAppr.parseData(str);

                        // 고객정보 확인
                        String custId = authService.getCustIdByCi(cardAppr.getCI());

                        // 마이데이터 경비 테이블 저장
                        saveMydataOutgoing(new MydataOutgoing().convertByCardAppr(custId, cardAppr));

                        // 고객정보상세 자산변경일시 업데이트
                        custService.updateCustMydataDt(custId);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 고객번호로 마이데이터 수입정보 삭제
     * @param custId
     * @return
     */
    public int deleteMydataIncomeByCustId(String custId) {
        return mydataIncomeRepository.deleteByCustId(custId);
    }

    /**
     * 고객번호로 마이데이터 경비정보 삭제
     * @param custId
     * @return
     */
    public int deleteMydataOutgoingByCustId(String custId) {
        return mydataOutgoingRepository.deleteByCustId(custId);
    }
}
