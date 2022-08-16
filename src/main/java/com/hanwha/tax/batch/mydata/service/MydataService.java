package com.hanwha.tax.batch.mydata.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.mydata.model.AbstractMydataCoocon;
import com.hanwha.tax.batch.mydata.model.BankTrans;
import com.hanwha.tax.batch.mydata.model.MydataIncome;
import com.hanwha.tax.batch.mydata.repository.MydataRepository;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jdk.jshell.execution.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.hanwha.tax.batch.Constants.BANK_TRANS_FILE;

@Slf4j
@Service("mydataService")
public class MydataService {

    @Autowired
    MydataRepository mydataRepository;

    @Autowired
    AuthService authService;

    @Value("${tax.sftp.user}")
    private String mydataSftpUser;

    @Value("${tax.sftp.pwd}")
    private String mydataSftpPwd;

    @Value("${tax.sftp.host}")
    private String mydataSftpHost;

    @Value("${tax.sftp.port}")
    private int mydataSftpPort;


    public void batchDataJob() {
        String yesterday = Utils.getYesterday(); //yyyymmdd = "20220705";//kkk
        String down_path = "D:/dev/hanwha/nas/tax/down/";
        String mydata_path = "D:/dev/hanwha/nas/tax/mydata/";

        // SFTP Get 수행 (/nas/tax/down)
        mydataSftpGet(down_path);

        // zip 압축 해제 (/nas/tax/mydata/yyyymmdd/*)
        mydata_path = mydata_path.concat("/".concat(yesterday)) + "/";
        mydataUnzip(down_path.concat(yesterday.concat("_IS000001_01_BANK_TRANS.zip")), mydata_path);
        mydataUnzip(down_path.concat(yesterday.concat("_IS000001_01_CARD_APPR.zip")), mydata_path);

        // File load (parsing)
        // DB Upsert (mydata_income)
        mydataIncomeLoad(mydata_path.concat(yesterday + "_IS000001_01_BANK_TRANS"));
        mydataOutgoingLoad(mydata_path.concat(yesterday + "_IS000001_01_CARD_APPR"));

    }


    private void mydataSftpGet(String downPath) {
        String yyyymmdd = Utils.getYesterday(); //yyyymmdd = "20220705";//kkk
        String sourceFile1 = "/data/".concat(yyyymmdd) + "/" + yyyymmdd + "_IS000001_01_BANK_TRANS.zip";
        String sourceFile2 = "/data/".concat(yyyymmdd) + "/" + yyyymmdd + "_IS000001_01_CARD_APPR.zip";
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
            FileOutputStream fo1 = new FileOutputStream(new File(downPath.concat(yyyymmdd.concat("_IS000001_01_BANK_TRANS.zip"))));
            channelSftp.get(sourceFile1, fo1);

            // down card
            FileOutputStream fo2 = new FileOutputStream(new File(downPath.concat(yyyymmdd.concat("_IS000001_01_CARD_APPR.zip"))));
            channelSftp.get(sourceFile2, fo2);
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
        List<MydataIncome> listMydataIncome = mydataRepository.findByCustIdAndOrgCodeAndAccountNumAndTransDtime(mydataIncome.getCustId(), mydataIncome.getOrgCode(), mydataIncome.getAccountNum(), mydataIncome.getTransDtime());

        if (listMydataIncome.size() != 1) {
            return null;
        }

        mydataIncome.setId(listMydataIncome.get(0).getId());
        mydataIncome.setCreateDt(listMydataIncome.get(0).getCreateDt());

        return mydataRepository.save(mydataIncome);
    }

    /**
     * 쿠콘 마이데이터 은행(수입) 파일 DB save
     * @param source_file
     */
    private void mydataIncomeLoad(String source_file) {
        // 마이데이터 은행(수입) 클래스 생성
        BankTrans bankTrans = (BankTrans) getMydataObjByName(BANK_TRANS_FILE);
        if (bankTrans == null) {
            log.error("쿠콘 마이데이터 은행(수입) 킄래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 마이데이터 파일 읽기
        try {
            BufferedReader reader = new BufferedReader(new FileReader(source_file));

            String str;
            boolean isIng = false;
            while ((str = reader.readLine()) != null) {
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
                        String cust_id = authService.getCustIdByCi(bankTrans.getCI());

                        // 마이데이터 수입 테이블 저장
                        saveMydataIncome(new MydataIncome().convertByBankTrans(cust_id, bankTrans));

                        log.info("## mydataIncomeLoad : {}", str);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mydataOutgoingLoad(String source_file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(source_file));

            String str;
            boolean isIng = false;
            while ((str = reader.readLine()) != null) {
                String[] vals = str.split("|");
                if (vals == null) break;
                if (vals[0].equals("ST")) {
                    isIng = true;
                } else if (vals[0].equals("ED")) {
                    break;
                } else {
                    // 본처리
                    if (isIng) {
                        String[] data = str.split("|");
                        if (data == null) break;

                        log.info("## mydataOutgoingLoad : {}", str);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
