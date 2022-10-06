package com.hanwha.tax.batch.mydata.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.entity.MydataIncome;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.mydata.model.AbstractMydataCoocon;
import com.hanwha.tax.batch.mydata.model.BankTrans;
import com.hanwha.tax.batch.mydata.model.CardAppr;
import com.hanwha.tax.batch.mydata.model.Thirdparty;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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


    /**
     * 마이데이터 파일 경로 가져오기
     * @param path
     * @return
     */
    private String getFilePath(String path, String ymdBasic) {
        StringBuilder filePath = new StringBuilder();

        filePath.append(path);
        filePath.append(ymdBasic);
        filePath.append("/");

        return filePath.toString();
    }

    /**
     * 경로 유무 확인
     * @param path
     */
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

    /**
     * SFTP 파일 다운로드
     * @param sftpPath
     * @param downPath
     * @param fileName
     */
    private void mydataSftpGet(String sftpPath, String downPath, String fileName) {
        checkPath(downPath);

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

            // down file
            fileName += ".zip";
            FileOutputStream fo = new FileOutputStream(new File(downPath+fileName));
            channelSftp.get(sftpPath+fileName, fo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channelSftp.disconnect();
            session.disconnect();
        }
    }

    /**
     * ZIP 파일 압축 해제
     * @param source_file
     * @param target_path
     */
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

    /**
     * 쿠콘 마이데이터 전문 공통부 추상클래스 매핑
     * @param model_name
     * @return
     */
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

        String[] columnArr = row_header.split("\\|");

        if (columnArr.length != 4) {
            log.error("헤더 데이터 수가 일치하지 않습니다.");
            return false;
        }

        if (!AbstractMydataCoocon.ROW_TYPE.헤더레코드부.getCode().equals(columnArr[0])) {
            log.error("헤더의 식별코드를 확인해 주시기 바랍니다.");
            return false;
        }

        if (!mydataSftpUser.equals(columnArr[2])) {
            log.error("헤더의 기관코드를 확인해 주시기 바랍니다.");
            return false;
        }

        return true;
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

    /**
     * 고객번호로 마이데이터 수입/경비정보 삭제
     * @param custId
     */
    public void deleteMydataByCustId(String custId) {
        log.info("▶▶▶▶▶▶ 마이데이터 수입정보 식제 건수 : {} 건", deleteMydataIncomeByCustId(custId));
        log.info("▶▶▶▶▶▶ 마이데이터 경비정보 식제 건수 : {} 건", deleteMydataOutgoingByCustId(custId));
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(수입))
     * @param row
     */
    private void saveMydata_BANK_TRANS(String row) {
        // 마이데이터 은행(수입) 클래스 생성
        BankTrans bankTrans = (BankTrans) getMydataObjByName(BANK_TRANS_FILE);
        if (bankTrans == null) {
            log.error("쿠콘 마이데이터 은행(수입) 클래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 마이데이터 은행(수입) 전문 파싱
        bankTrans.parseData(row);

        // 고객정보 확인
        String custId = authService.getCustIdByCi(bankTrans.getCI());

        if (Utils.isEmpty(custId)) {
            log.error("CI 값에 해당하는 고객정보가 존재하지 않습니다. [{}]", bankTrans.getCI());
            return;
        }

        // 마이데이터 수입 정보 조회
        MydataIncome mydataIncome = new MydataIncome().convertByBankTrans(custId, bankTrans);
        List<MydataIncome> listMydataIncome = mydataIncomeRepository.findByDataPk(mydataIncome);

        if (1 < listMydataIncome.size()) {
            log.error("마이데이터 수입 정보가 올바르지 않습니다.");
            return;
        }

        if (0 < listMydataIncome.size()) {
            mydataIncome.setId(listMydataIncome.get(0).getId());
            mydataIncome.setCreateDt(listMydataIncome.get(0).getCreateDt());
            mydataIncome.setUpdateDt(Utils.getCurrentDateTime());
        }

        // 마이데이터 수입 테이블 저장
        mydataIncomeRepository.save(mydataIncome);

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(경비))
     * @param row
     */
    private void saveMydata_CARD_APPR(String row) {
        // 마이데이터 카드(경비) 클래스 생성
        CardAppr cardAppr = (CardAppr) getMydataObjByName(CARD_APPR_FILE);
        if (cardAppr == null) {
            log.error("쿠콘 마이데이터 카드(경비) 클래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 마이데이터 카드(경비) 전문 파싱
        cardAppr.parseData(row);

        // 고객정보 확인
        String custId = authService.getCustIdByCi(cardAppr.getCI());

        if (Utils.isEmpty(custId)) {
            log.error("CI 값에 해당하는 고객정보가 존재하지 않습니다. [{}]", cardAppr.getCI());
            return;
        }

        // 마이데이터 경비 정보 조회
        MydataOutgoing mydataOutgoing = new MydataOutgoing().convertByCardAppr(custId, cardAppr);
        List<MydataOutgoing> listMydataOutgoing = mydataOutgoingRepository.findByDataPk(mydataOutgoing);

        if (1 < listMydataOutgoing.size()) {
            log.error("마이데이터 경비 정보가 올바르지 않습니다.");
            return;
        }

        if (0 < listMydataOutgoing.size()) {
            mydataOutgoing.setId(listMydataOutgoing.get(0).getId());
            mydataOutgoing.setCreateDt(listMydataOutgoing.get(0).getCreateDt());
            mydataOutgoing.setUpdateDt(Utils.getCurrentDateTime());
        }

        // 마이데이터 경비 테이블 저장
        mydataOutgoingRepository.save(mydataOutgoing);

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (제3자 제공동의)
     * @param row
     */
    private void saveMydata_THIRDPARTY(String row) {
        // 마이데이터 제3자 제공동의 회원 클래스 생성
        Thirdparty thirdparty = (Thirdparty) getMydataObjByName(THIRDPARTY_FILE);
        if (thirdparty == null) {
            log.error("제3자 제공동의 회원 클래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 제3자 제공동의 회원 전문 파싱
        thirdparty.parseData(row);

        // 고객정보 확인
        String custId = authService.getCustIdByCi(thirdparty.getCI());

        if (Utils.isEmpty(custId)) {
            log.error("CI 값에 해당하는 고객정보가 존재하지 않습니다. [{}]", thirdparty.getCI());
            return;
        }

        // 고객정보 조회
        Cust cust = custService.getCust(custId).orElse(null);

        if (cust == null) {
            log.error("고객정보가 존재하지 않습니다. [{}]", custId);
            return;
        }

        String currentDate = Utils.getCurrentDateTime();    // 현재일시

        // 제3자 제공동의 동의한 경우
        if ("Y".equals(thirdparty.get쿠콘제3자제공동의1()) && !"3".equals(thirdparty.get변경구분())) {
            // 준회원인데 제3자 제공 동의한 경우
            if (Cust.CustGrade.준회원.getCode().equals(cust.getCustGrade())) {
                log.info("▶︎▶︎▶︎ 정회원 등급 대상 고객 : {}", cust.getCustId());
                cust.setCustGrade(Cust.CustGrade.정회원.getCode());
                cust.setRegInDt(currentDate);
                custService.modifyCust(cust);
            }
        }
        // 제3자 제공동의 미동의 경우
        else {
            // 정회원인데 제3자 제공 미동의한 경우
            if (Cust.CustGrade.정회원.getCode().equals(cust.getCustGrade())) {
                log.info("▶︎▶︎▶︎ 준회원 등급 대상 고객 : {}", cust.getCustId());
                cust.setCustGrade(Cust.CustGrade.준회원.getCode());
                cust.setRegOutDt(currentDate);
                custService.modifyCust(cust);
                
                // 마이데이터 정보 파기
                deleteMydataByCustId(cust.getCustId());
                
                // 마이데이터 정보 파기에 따른 TOTAL 정보 업데이트
                // ★★★ TOTAL 테이블 명확해지면 작업 예정
            }
        }
    }

    /**
     * 마이데이터 파일 읽기
     * @param fileType
     * @param sourceFile
     */
    private void readMydataFile(String fileType, String sourceFile) {
        // 마이데이터 파일 읽기
        try {
            BufferedReader reader = new BufferedReader(new FileReader(sourceFile));

            String str;
            boolean isIng = false;
            while ((str = reader.readLine()) != null) {
                log.debug("## row : {}", str);
                String[] vals = str.split("\\|");
                if (vals == null) break;
                if (AbstractMydataCoocon.ROW_TYPE.헤더레코드부.getCode().equals(vals[0])) {
                    isIng = true;

                    // 헤더 검증
                    if (!validateHeader(str))
                        return;
                } else if (AbstractMydataCoocon.ROW_TYPE.테일레코드부.getCode().equals(vals[0])) {
                    break;
                } else {
                    // 본처리
                    if (isIng) {
                        try {
                            Method method = this.getClass().getDeclaredMethod("saveMydata_"+fileType, String.class);
                            method.invoke(this, str);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 쿠콘 마이데이터 처리
     * @param fileType
     */
    public void procMydataInfo(String fileType, String ymdBasic) {
        String downPath = getFilePath(COOCON_FILE_DOWNLOAD_PATH, ymdBasic);   // 다운로드 파일 경로
        String mydataPath = getFilePath(COOCON_FILE_MYDATA_PATH, ymdBasic);   // 마이데이터 파일 경로
        String fileName = ymdBasic + "_" + mydataSftpUser + "_" + AbstractMydataCoocon.FILE_KIND.쿠콘.getCode() + "_" + fileType;

        // SFTP Get 수행 (/nas/tax/down)
        log.info("▶︎▶︎▶︎ SFTP 파일 다운로드 시작");
        mydataSftpGet(getFilePath(COOCON_FILE_SFTP_PATH, ymdBasic), downPath, fileName);

        // zip 압축 해제 (/nas/tax/mydata/yyyymmdd/*)
        log.info("▶︎▶︎▶︎ BATCH 파일 압축 풀기 시작");
        mydataUnzip(downPath+fileName+".zip", mydataPath);

        // File load (parsing)
        // DB Upsert (mydata_income)
        log.info("▶︎▶︎▶︎ {} 파일 읽기 시작", fileType);
        readMydataFile(fileType, mydataPath+fileName);
    }

    /**
     * 카드정보로 특정 카드이력 내역 조회
     * @param orgCode
     * @param cardId
     * @param apprNum
     * @return
     */
    public List<MydataOutgoing> getMydataOutgoingByCardInfo(String orgCode, String cardId, String apprNum) {
        return mydataOutgoingRepository.findByOrgCodeAndCardIdAndApprNumOrderByTransDtimeAscSeqAsc(orgCode, cardId, apprNum);
    }
}
