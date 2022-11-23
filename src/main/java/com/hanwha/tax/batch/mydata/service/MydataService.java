package com.hanwha.tax.batch.mydata.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.*;
import com.hanwha.tax.batch.mydata.model.*;
import com.hanwha.tax.batch.mydata.repository.*;
import com.hanwha.tax.batch.tax.repository.TaxRepository;
import com.hanwha.tax.batch.tax.service.CalcTax;
import com.hanwha.tax.batch.tax.service.TaxService;
import com.hanwha.tax.batch.total.repository.TotalIncomeRepository;
import com.hanwha.tax.batch.total.repository.TotalOutgoingRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.hanwha.tax.batch.Constants.COOCON_FILE_SFTP_PATH;

@Slf4j
@Service("mydataService")
public class MydataService {

    @Autowired
    MydataIncomeRepository mydataIncomeRepository;

    @Autowired
    MydataOutgoingRepository mydataOutgoingRepository;

    @Autowired
    MydataBankBa01Repository mydataBankBa01Repository;

    @Autowired
    MydataBankBa02Repository mydataBankBa02Repository;

    @Autowired
    MydataBankBa03Repository mydataBankBa03Repository;

    @Autowired
    MydataBankBa04Repository mydataBankBa04Repository;

    @Autowired
    MydataBankBa11Repository mydataBankBa11Repository;

    @Autowired
    MydataBankBa12Repository mydataBankBa12Repository;

    @Autowired
    MydataBankBa13Repository mydataBankBa13Repository;

    @Autowired
    MydataBankBa21Repository mydataBankBa21Repository;

    @Autowired
    MydataBankBa22Repository mydataBankBa22Repository;

    @Autowired
    MydataBankBa23Repository mydataBankBa23Repository;

    @Autowired
    MydataCardCd01Repository mydataCardCd01Repository;

    @Autowired
    MydataCardCd11Repository mydataCardCd11Repository;

    @Autowired
    MydataCardCd21Repository mydataCardCd21Repository;

    @Autowired
    MydataCardCd22Repository mydataCardCd22Repository;

    @Autowired
    MydataCardCd23Repository mydataCardCd23Repository;

    @Autowired
    MydataCardCd24Repository mydataCardCd24Repository;

    @Autowired
    MydataCardCd03Repository mydataCardCd03Repository;

    @Autowired
    MydataCardCd04Repository mydataCardCd04Repository;

    @Autowired
    MydataCardCd31Repository mydataCardCd31Repository;

    @Autowired
    MydataCardCd32Repository mydataCardCd32Repository;

    @Autowired
    MydataCardCd33Repository mydataCardCd33Repository;

    @Autowired
    TotalIncomeRepository totalIncomeRepository;

    @Autowired
    TotalOutgoingRepository totalOutgoingRepository;

    @Autowired
    TaxRepository taxRepository;

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
     * @return
     */
    public MydataIncome saveMydataIncome(MydataIncome mydataIncome) {
        Integer seq = mydataIncome.getSeq();

        // 마이데이터 수입 원본 데이터 조회
        mydataIncome.setSeq(null);
        List<MydataIncome> listMydataIncome = mydataIncomeRepository.findByDataPk(mydataIncome);

        List<MydataIncome> mydataIncomeOri = new ArrayList<>();     // 원본데이터
        List<MydataIncome> mydataIncomePk = new ArrayList<>();      // 기존데이터

        // 마이데이터 수입 원본/단건 데이터 조회
        for (MydataIncome mi : listMydataIncome) {
            if (mi.getSeq() == null)   mydataIncomeOri.add(mi);
            if (mi.getSeq() == seq)  mydataIncomePk.add(mi);
        }
        mydataIncome.setSeq(seq);

        if (1 < mydataIncomeOri.size()) {
            log.error("※※※ 마이데이터 수입 원본 데이터가 올바르지 않습니다.\n[{}]", mydataIncome.toString());
            return null;
        }

        if (1 < mydataIncomePk.size()) {
            log.error("※※※ 마이데이터 수입 중복 데이터가 존재합니다.\n[{}]", mydataIncome.toString());
            return null;
        }

        // 기존 매핑된 마이데이터 이력이 있고 등록할 데이터가 원본 데이터인 경우 skip
        if (0 < listMydataIncome.size() && mydataIncomeOri.size() == 0 && mydataIncomePk.size() == 0) {
            log.debug("▶▶▶︎ 기존 데이터가 존재하여 원본 데이터 저장하지 않습니다.\n[{}]", mydataIncome.toString());
            return null;
        }

        // 원본데이터가 없고 매핑된 마이데이터를 등록하는 경우 skip
        if (listMydataIncome.size() == 0 && seq != null) {
            log.info("▶▶▶︎ 원본 데이터가 존재하지 않아 매핑 데이터 저장하지 않습니다.\n[{}]", mydataIncome.toString());
            return null;
        }

        // 기존 정보가 존재하는 경우 변경되지 않는 정보 세팅
        if (0 < mydataIncomeOri.size()) {
            mydataIncome.setId(mydataIncomeOri.get(0).getId());
            mydataIncome.setCreateDt(mydataIncomeOri.get(0).getCreateDt());
            mydataIncome.setUpdateDt(Utils.getCurrentDateTime());
        }
        if (0 < mydataIncomePk.size()) {
            mydataIncome.setId(mydataIncomePk.get(0).getId());
            mydataIncome.setCreateDt(mydataIncomePk.get(0).getCreateDt());
            mydataIncome.setUpdateDt(Utils.getCurrentDateTime());
        }

        // 마이데이터 수입 테이블 저장
        return mydataIncomeRepository.save(mydataIncome);
    }

    /**
     * 마이데이터 경비 정보 저장
     * @param mydataOutgoing
     * @return
     */
    private MydataOutgoing saveMydataOutgoing(MydataOutgoing mydataOutgoing) {
        Integer seq = mydataOutgoing.getSeq();

        // 마이데이터 경비 내역 조회
        mydataOutgoing.setSeq(null);
        List<MydataOutgoing> listMydataOutgoing = mydataOutgoingRepository.findByDataPk(mydataOutgoing);

        List<MydataOutgoing> mydataOutgoingOri = new ArrayList<>();     // 원본데이터
        List<MydataOutgoing> mydataOutgoingPk = new ArrayList<>();      // 기존데이터

        // 마이데이터 경비 원본/단건 데이터 조회
        for (MydataOutgoing mo : listMydataOutgoing) {
            if (mo.getSeq() == null)   mydataOutgoingOri.add(mo);
            if (mo.getSeq() == seq)  mydataOutgoingPk.add(mo);
        }
        mydataOutgoing.setSeq(seq);

        if (1 < mydataOutgoingOri.size()) {
            log.error("※※※ 마이데이터 경비 원본 데이터가 올바르지 않습니다.\n[{}]", mydataOutgoing.toString());
            return null;
        }

        if (1 < mydataOutgoingPk.size()) {
            log.error("※※※ 마이데이터 경비 중복 데이터가 존재합니다.\n[{}]", mydataOutgoing.toString());
            return null;
        }

        // 기존 매핑된 마이데이터 이력이 있고 등록할 데이터가 원본 데이터인 경우 skip
        if (0 < listMydataOutgoing.size() && mydataOutgoingOri.size() == 0 && mydataOutgoingPk.size() == 0) {
            log.debug("▶▶▶︎ 기존 데이터가 존재하여 원본 데이터 저장하지 않습니다.\n[{}]", mydataOutgoing.toString());
            return null;
        }

        // 원본데이터가 없고 매핑된 마이데이터를 등록하는 경우 skip
        if (listMydataOutgoing.size() == 0 && seq != null) {
            log.info("▶▶▶︎ 원본 데이터가 존재하지 않아 매핑 데이터 저장하지 않습니다.\n[{}]", mydataOutgoing.toString());
            return null;
        }

        // 기존 정보가 존재하는 경우 변경되지 않는 정보 세팅
        if (0 < mydataOutgoingOri.size()) {
            mydataOutgoing.setId(mydataOutgoingOri.get(0).getId());
            mydataOutgoing.setCreateDt(mydataOutgoingOri.get(0).getCreateDt());
            mydataOutgoing.setUpdateDt(Utils.getCurrentDateTime());
        }
        if (0 < mydataOutgoingPk.size()) {
            mydataOutgoing.setId(mydataOutgoingPk.get(0).getId());
            mydataOutgoing.setCreateDt(mydataOutgoingPk.get(0).getCreateDt());
            mydataOutgoing.setUpdateDt(Utils.getCurrentDateTime());
        }

        // 마이데이터 경비 테이블 저장
        return mydataOutgoingRepository.save(mydataOutgoing);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 계좌 목록)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA01(String modelName, String row) {
        // 은행(원본) : 은행 계좌 목록 저장
        if (mydataBankBa01Repository.save(new MydataBankBa01(row)) == null) {
            log.error("은행(원본) : 은행 계좌 목록 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 수신계좌 기본정보)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA02(String modelName, String row) {
        // 은행(원본) : 은행 수신계좌 기본정보 저장
        if (mydataBankBa02Repository.save(new MydataBankBa02(row)) == null) {
            log.error("은행(원본) : 은행 수신계좌 기본정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 수신계좌 추가정보)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA03(String modelName, String row) {
        // 은행(원본) : 은행 수신계좌 추가정보 저장
        if (mydataBankBa03Repository.save(new MydataBankBa03(row)) == null) {
            log.error("은행(원본) : 은행 수신계좌 추가정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 수신 계좌 거래내역)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA04(String modelName, String row) {
        // 은행(원본) : 은행 수신 계좌 거래내역 저장
        if (mydataBankBa04Repository.save(new MydataBankBa04(row)) == null) {
            log.error("은행(원본) : 은행 수신 계좌 거래내역 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }

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
        saveMydataIncome(new MydataIncome().convertByBank(custId, bank));

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 투자 계좌 기본정보)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA11(String modelName, String row) {
        // 은행(원본) : 은행 투자 계좌 기본정보 저장
        if (mydataBankBa11Repository.save(new MydataBankBa11(row)) == null) {
            log.error("은행(원본) : 은행 투자 계좌 기본정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 투자 계좌 상세정보)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA12(String modelName, String row) {
        // 은행(원본) : 은행 투자 계좌 상세정보 저장
        if (mydataBankBa12Repository.save(new MydataBankBa12(row)) == null) {
            log.error("은행(원본) : 은행 투자 계좌 상세정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 투자 계좌 거래내역)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA13(String modelName, String row) {
        // 은행(원본) : 은행 투자 계좌 거래내역 저장
        if (mydataBankBa13Repository.save(new MydataBankBa13(row)) == null) {
            log.error("은행(원본) : 은행 투자 계좌 거래내역 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 대출 계좌 기본정보)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA21(String modelName, String row) {
        // 은행(원본) : 은행 대출 계좌 기본정보 저장
        if (mydataBankBa21Repository.save(new MydataBankBa21(row)) == null) {
            log.error("은행(원본) : 은행 대출 계좌 기본정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 대출 계좌 추가정보)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA22(String modelName, String row) {
        // 은행(원본) : 은행 대출 계좌 추가정보 저장
        if (mydataBankBa22Repository.save(new MydataBankBa22(row)) == null) {
            log.error("은행(원본) : 은행 대출 계좌 추가정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (은행(원본) : 은행 대출 계좌 거래내역)
     * @param modelName
     * @param row
     */
    private void saveMydataBankBA23(String modelName, String row) {
        // 은행(원본) : 은행 대출 계좌 거래내역 저장
        if (mydataBankBa23Repository.save(new MydataBankBa23(row)) == null) {
            log.error("은행(원본) : 은행 대출 계좌 거래내역 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 카드 목록)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD01(String modelName, String row) {
        // 카드(원본) : 카드 목록 저장
        if (mydataCardCd01Repository.save(new MydataCardCd01(row)) == null) {
            log.error("카드(원본) : 카드 목록 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 포인트 정보)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD11(String modelName, String row) {
        // 카드(원본) : 포인트 정보 저장
        if (mydataCardCd11Repository.save(new MydataCardCd11(row)) == null) {
            log.error("카드(원본) : 포인트 정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 청구 기본정보)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD21(String modelName, String row) {
        // 카드(원본) : 청구 기본정보 저장
        if (mydataCardCd21Repository.save(new MydataCardCd21(row)) == null) {
            log.error("카드(원본) : 청구 기본정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 청구 추가정보)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD22(String modelName, String row) {
        // 카드(원본) : 청구 추가정보 저장
        if (mydataCardCd22Repository.save(new MydataCardCd22(row)) == null) {
            log.error("카드(원본) : 청구 추가정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 결제정보)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD23(String modelName, String row) {
        // 카드(원본) : 결제정보 저장
        if (mydataCardCd23Repository.save(new MydataCardCd23(row)) == null) {
            log.error("카드(원본) : 결제정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 리볼빙 정보)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD24(String modelName, String row) {
        // 카드(원본) : 리볼빙 정보 저장
        if (mydataCardCd24Repository.save(new MydataCardCd24(row)) == null) {
            log.error("카드(원본) : 리볼빙 정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 국내 승인내역)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD03(String modelName, String row) {
        // 카드(원본) : 국내 승인내역 저장
        if (mydataCardCd03Repository.save(new MydataCardCd03(row)) == null) {
            log.error("카드(원본) : 국내 승인내역 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }

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
        saveMydataOutgoing(new MydataOutgoing().convertByCard(custId, card));

        // 고객정보상세 자산변경일시 업데이트
        custService.updateCustMydataDt(custId);
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 해외 승인내역)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD04(String modelName, String row) {
        // 카드(원본) : 해외 승인내역 저장
        if (mydataCardCd04Repository.save(new MydataCardCd04(row)) == null) {
            log.error("카드(원본) : 해외 승인내역 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 대출상품 목록)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD31(String modelName, String row) {
        // 카드(원본) : 대출상품 목록 저장
        if (mydataCardCd31Repository.save(new MydataCardCd31(row)) == null) {
            log.error("카드(원본) : 대출상품 목록 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 단기대출 정보)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD32(String modelName, String row) {
        // 카드(원본) : 단기대출 정보 저장
        if (mydataCardCd32Repository.save(new MydataCardCd32(row)) == null) {
            log.error("카드(원본) : 단기대출 정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
    }

    /**
     * 마이데이터 파일 ROW 별 파싱 및 저장 (카드(원본) : 장기대출 정보)
     * @param modelName
     * @param row
     */
    private void saveMydataCardCD33(String modelName, String row) {
        // 카드(원본) : 장기대출 정보 저장
        if (mydataCardCd33Repository.save(new MydataCardCd33(row)) == null) {
            log.error("카드(원본) : 장기대출 정보 저장에 실패하였습니다.\n[Mydata{}][{}]", modelName, row);
        }
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
        saveMydataIncome(new MydataIncome().convertByBankTrans(custId, bankTrans));

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
        saveMydataOutgoing(new MydataOutgoing().convertByCardAppr(custId, cardAppr));

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

    public void resetMydata(String ymd) {
        // 은행(원본) 정보 삭제
        mydataBankBa01Repository.deleteBA01(ymd);
        mydataBankBa01Repository.deleteBA02(ymd);
        mydataBankBa01Repository.deleteBA03(ymd);
        mydataBankBa01Repository.deleteBA04(ymd);
        mydataBankBa01Repository.deleteBA11(ymd);
        mydataBankBa01Repository.deleteBA12(ymd);
        mydataBankBa01Repository.deleteBA13(ymd);
        mydataBankBa01Repository.deleteBA21(ymd);
        mydataBankBa01Repository.deleteBA22(ymd);
        mydataBankBa01Repository.deleteBA23(ymd);
        // 카드(원본) 정보 삭제
        mydataCardCd01Repository.deleteCD01(ymd);
        mydataCardCd01Repository.deleteCD03(ymd);
        mydataCardCd01Repository.deleteCD04(ymd);
        mydataCardCd01Repository.deleteCD11(ymd);
        mydataCardCd01Repository.deleteCD21(ymd);
        mydataCardCd01Repository.deleteCD22(ymd);
        mydataCardCd01Repository.deleteCD23(ymd);
        mydataCardCd01Repository.deleteCD24(ymd);
        mydataCardCd01Repository.deleteCD31(ymd);
        mydataCardCd01Repository.deleteCD32(ymd);
        mydataCardCd01Repository.deleteCD33(ymd);
//        // 은행(수입) 정보 삭제
//        mydataIncomeRepository.deleteMydataIncome();
//        // 카드(경비) 정보 삭제
//        mydataOutgoingRepository.deleteMydataOutgoing();
//        // total(수입) 정보 삭제
//        totalIncomeRepository.deleteTotalIncome();
//        // total(경비) 정보 삭제
//        totalOutgoingRepository.deleteTotalOutgoing();
//        // tax 정보 삭제
//        taxRepository.deleteTax();
    }

    /**
     * 은행(수입) 중복 내역 조회
     * @return
     */
    public List<Map<String,String>> getMydataIncomeDuplicate() {
        return mydataIncomeRepository.getMydataIncomeDuplicate();
    }

    /**
     * 카드(경비) 중복 내역 조회
     * @return
     */
    public List<Map<String,String>> getMydataOutgoingDuplicate() {
        return mydataOutgoingRepository.getMydataOutgoingDuplicate();
    }

    /**
     * 고객번호로 은행(수입) 내역 조회
     * @return
     */
    public List<MydataIncome> getMydataIncomeByCustId(String custId) {
        return mydataIncomeRepository.findByCustId(custId);
    }

    /**
     * 고객번호로 카드(경비) 내역 조회
     * @return
     */
    public List<MydataOutgoing> getMydataOutgoingByCustId(String custId) {
        return mydataOutgoingRepository.findByCustId(custId);
    }
}