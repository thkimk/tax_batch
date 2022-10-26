package com.hanwha.tax.batch.mydata.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.entity.MydataIncome;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.mydata.model.*;
import com.hanwha.tax.batch.mydata.repository.MydataIncomeRepository;
import com.hanwha.tax.batch.mydata.repository.MydataOutgoingRepository;
import com.hanwha.tax.batch.tax.service.CalcTax;
import com.hanwha.tax.batch.tax.service.TaxService;
import com.hanwha.tax.batch.total.service.TotalService;
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

    @Autowired
    TotalService totalService;

    @Autowired
    TaxService taxService;

    @Autowired
    CalcTax calcTax;

    @Value("${tax.sftp.user}")
    private String mydataSftpUser;

    @Value("${tax.sftp.pwd}")
    private String mydataSftpPwd;

    @Value("${tax.sftp.host}")
    private String mydataSftpHost;

    @Value("${tax.sftp.port}")
    private int mydataSftpPort;

    @Value("${tax.mydata.path.zip}")
    private String mydataFileZipPath;

    @Value("${tax.mydata.path.unzip}")
    private String mydataFileUnzipPath;

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
     * @param modelName
     * @return
     */
    private AbstractMydataCoocon getMydataObjByName(String modelName) {
        if (Utils.isEmpty(modelName))
            return null;

        AbstractMydataCoocon info = null;
        try {
            Class<?> c = Class.forName("com.hanwha.tax.batch.mydata.model." + modelName);
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
     * 마이데이터 수입 정보 저장
     * @param mydataIncome
     * @param isOrigin
     * @return
     */
    private MydataIncome saveMydataIncome(MydataIncome mydataIncome, boolean isOrigin) {
        // 마이데이터 수입 정보 조회
        List<MydataIncome> listMydataIncome = mydataIncomeRepository.findByDataPk(mydataIncome);

        if (1 < listMydataIncome.size()) {
            log.error("마이데이터 수입 정보가 올바르지 않습니다.\n[{}]", mydataIncome.toString());
            return null;
        }

        if (0 < listMydataIncome.size()) {
            mydataIncome.setId(listMydataIncome.get(0).getId());
            mydataIncome.setCreateDt(listMydataIncome.get(0).getCreateDt());
            mydataIncome.setUpdateDt(Utils.getCurrentDateTime());

            // 쿠콘 원본데이터인 경우 수입여부, 3.3프로 포함여부 항목 세팅
            if (isOrigin) {
                mydataIncome.setIsIncome(listMydataIncome.get(0).getIsIncome());
                mydataIncome.setIs33(listMydataIncome.get(0).getIs33());
            }
        }

        // 마이데이터 수입 테이블 저장
        return mydataIncomeRepository.save(mydataIncome);
    }

    /**
     * 마이데이터 경비 정보 저장
     * @param mydataOutgoing
     * @param isOrigin
     * @return
     */
    private MydataOutgoing saveMydataOutgoing(MydataOutgoing mydataOutgoing, boolean isOrigin) {
        // 마이데이터 경비 정보 조회
        List<MydataOutgoing> listMydataOutgoing = mydataOutgoingRepository.findByDataPk(mydataOutgoing);

        if (1 < listMydataOutgoing.size()) {
            log.error("마이데이터 경비 정보가 올바르지 않습니다.\n[{}]", mydataOutgoing.toString());
            return null;
        }

        if (0 < listMydataOutgoing.size()) {
            mydataOutgoing.setId(listMydataOutgoing.get(0).getId());
            mydataOutgoing.setCreateDt(listMydataOutgoing.get(0).getCreateDt());
            mydataOutgoing.setUpdateDt(Utils.getCurrentDateTime());

            // 쿠콘 원본데이터인 경우 카테고리 항목 세팅
            if (isOrigin) {
                mydataOutgoing.setCategory(listMydataOutgoing.get(0).getCategory());
            }
        }

        // 마이데이터 경비 테이블 저장
        return mydataOutgoingRepository.save(mydataOutgoing);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 수신 계좌 거래내역)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA04(String modelName, String row) {
        // 마이데이터 은행(원본) 클래스 생성
        BankBA04 bank = (BankBA04) getMydataObjByName(modelName);
        if (bank == null) {
            log.error("쿠콘 마이데이터 은행(원본) 수신 계좌 거래내역 클래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 마이데이터 은행(원본) 전문 파싱
        bank.parseData(row);

        // 고객정보 확인
        String custId = authService.getCustIdByCi(bank.getCI());

        if (Utils.isEmpty(custId)) {
            log.error("CI 값에 해당하는 고객정보가 존재하지 않습니다. [{}]", bank.getCI());
            return;
        }

        // 마이데이터 수입 정보 저장
        saveMydataIncome(new MydataIncome().convertByBank(custId, bank), true);

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 국내 승인내역)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD03(String modelName, String row) {
        // 마이데이터 카드(원본) 클래스 생성
        CardCD03 card = (CardCD03) getMydataObjByName(modelName);
        if (card == null) {
            log.error("쿠콘 마이데이터 카드(원본) 국내 승인내역 클래스를 확인해 주시기 바랍니다.");
            return;
        }

        // 마이데이터 카드(원본) 전문 파싱
        card.parseData(row);

        // 고객정보 확인
        String custId = authService.getCustIdByCi(card.getCI());

        if (Utils.isEmpty(custId)) {
            log.error("CI 값에 해당하는 고객정보가 존재하지 않습니다. [{}]", card.getCI());
            return;
        }

        // 마이데이터 경비 정보 저장
        saveMydataOutgoing(new MydataOutgoing().convertByCard(custId, card), true);

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(수입))
     * @param modelName
     * @param row
     */
    private void saveMydataBankTransBT01(String modelName, String row) {
        // 마이데이터 은행(수입) 클래스 생성
        BankTransBT01 bankTrans = (BankTransBT01) getMydataObjByName(modelName);
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

        // 마이데이터 수입 정보 저장
        saveMydataIncome(new MydataIncome().convertByBankTrans(custId, bankTrans), false);

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(경비))
     * @param modelName
     * @param row
     */
    private void saveMydataCardApprCA01(String modelName, String row) {
        // 마이데이터 카드(경비) 클래스 생성
        CardApprCA01 cardAppr = (CardApprCA01) getMydataObjByName(modelName);
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

        // 마이데이터 경비 정보 저장
        saveMydataOutgoing(new MydataOutgoing().convertByCardAppr(custId, cardAppr), false);

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (제3자 제공동의)
     * @param modelName
     * @param row
     */
    private void saveMydataThirdpartyCI(String modelName, String row) {
        // 마이데이터 제3자 제공동의 회원 클래스 생성
        ThirdpartyCI thirdparty = (ThirdpartyCI) getMydataObjByName(modelName);
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
                log.info("▶▶▶▶▶▶ 마이데이터 TOTAL 수입정보 식제 건수 : {} 건", totalService.deleteTotalIncomeByCustIdAndFlagFk(cust.getCustId()));
                log.info("▶▶▶▶▶▶ 마이데이터 TOTAL 경비정보 식제 건수 : {} 건", totalService.deleteTotalOutgoingByCustIdAndFlagFk(cust.getCustId()));

                // 해당 고객의 연도 별 수입이력 조회
                totalService.getTotalIncomeByCustId(cust.getCustId()).forEach(t -> {
                    // 소득세 정보 갱신
                    taxService.saveTax(t.getCustId(), t.getYear(), calcTax);
                });
            }
        }
    }

    /**
     * 마이데이터 파일 읽기
     * @param modelName
     * @param sourceFile
     */
    private void readMydataFile(String modelName, String sourceFile) {
        modelName = Utils.convertToCamelCase("_"+modelName);

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
                            Method method = this.getClass().getDeclaredMethod("saveMydata"+modelName+vals[1], String.class, String.class);
                            method.invoke(this, modelName+vals[1], str);
                        } catch (NoSuchMethodException e) {
                        } catch (IllegalAccessException | InvocationTargetException e) {
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
        String downPath = getFilePath(mydataFileZipPath, ymdBasic);         // 마이데이터 zip 파일 경로
        String mydataPath = getFilePath(mydataFileUnzipPath, ymdBasic);     // 마이데이터 unzio 파일 경로
        String fileName = ymdBasic + "_" + mydataSftpUser + "_" + AbstractMydataCoocon.FILE_KIND.쿠콘.getCode() + "_" + fileType;

        // SFTP Get 수행
        mydataSftpGet(getFilePath(COOCON_FILE_SFTP_PATH, ymdBasic), downPath, fileName);

        // zip 압축 해제
        mydataUnzip(downPath+fileName+".zip", mydataPath);

        // File load (parsing)
        // DB Upsert (mydata_income)
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