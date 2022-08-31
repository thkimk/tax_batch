package com.hanwha.tax.batch.cust.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.book.service.BookService;
import com.hanwha.tax.batch.cust.repository.*;
import com.hanwha.tax.batch.dev.service.DevService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.entity.CustInfoDtl;
import com.hanwha.tax.batch.helpdesk.service.HelpdeskService;
import com.hanwha.tax.batch.login.service.LoginService;
import com.hanwha.tax.batch.mydata.service.MydataService;
import com.hanwha.tax.batch.notice.service.NoticeService;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("custService")
public class CustService {

    @Autowired
    CustRepository custRepository;

    @Autowired
    CustDeductRepository custDeductRepository;

    @Autowired
    CustFamilyRepository custFamilyRepository;

    @Autowired
    CustInfoRepository custInfoRepository;

    @Autowired
    CustInfoDtlRepository custInfoDtlRepository;

    @Autowired
    CustTermsAgmtRepository custTermsAgmtRepository;

    @Autowired
    AuthService authService;

    @Autowired
    BookService bookService;

    @Autowired
    DevService devService;

    @Autowired
    HelpdeskService helpdeskService;

    @Autowired
    LoginService loginService;

    @Autowired
    MydataService mydataService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    TotalService totalService;

    /**
     * 고객상세정보의 자산변경일시 갱신
     * @param custId
     * @return
     */
    public CustInfoDtl updateCustMydataDt(String custId) {
        CustInfoDtl custInfoDtl = new CustInfoDtl();
        custInfoDtl.setCustId(custId);
        custInfoDtl.setMydataDt(Utils.getCurrentDateTime());

        return custInfoDtlRepository.save(custInfoDtl);
    }

    /**
     * 준회원 탈퇴 후 30일 지난 고객리스트
     * @return
     */
    public List<Cust> getCustAsctOutList() {
        return custRepository.findByCustStatusAndCustGradeAndAsctOutDtLessThan(Cust.CustStatus.탈회.getCode(), Cust.CustGrade.준회원.getCode(), Utils.addDays(Utils.getCurrentDate("yyyy-MM-dd"), -29)+" 00:00:00");
    }

    /**
     * 정회원 탈퇴 후 30일 지난 고객리스트
     * @return
     */
    public List<Cust> getCustRegOutist() {
        return custRepository.findByCustStatusAndCustGradeAndRegOutDtLessThan(Cust.CustStatus.탈회.getCode(), Cust.CustGrade.정회원.getCode(), Utils.addDays(Utils.getCurrentDate("yyyy-MM-dd"), -29)+" 00:00:00");
    }

    /**
     * 고객번호로 고객정보 삭제
     * @param custId
     * @return
     */
    public void deleteCustById(String custId) {
        custRepository.deleteById(custId);
    }

    /**
     * 고객번호로 고객 연도 별 자산정보 삭제
     * @param custId
     * @return
     */
    public int deleteCustDeductByCustId(String custId) {
        return custDeductRepository.deleteByCustId(custId);
    }

    /**
     * 고객번호로 고객가족정보 삭제
     * @param custId
     * @return
     */
    public int deleteCustFamilyByCustId(String custId) {
        return custFamilyRepository.deleteByCustId(custId);
    }

    /**
     * 고객번호로 고객정보 삭제
     * @param custId
     * @return
     */
    public void deleteCustInfoById(String custId) {
        custInfoRepository.deleteById(custId);
    }

    /**
     * 고객번호로 고객상세정보 삭제
     * @param custId
     * @return
     */
    public void deleteCustInfoDtlById(String custId) {
        custInfoDtlRepository.deleteById(custId);
    }

    /**
     * 고객번호로 고객약관동의 삭제
     * @param custId
     * @return
     */
    public int deleteCustTermsAgmtByCustId(String custId) {
        return custTermsAgmtRepository.deleteByCustId(custId);
    }

    /**
     * 고객 데이터 파기
     * @param custID
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustData(String custID) throws Exception {
        log.info("▶▶▶▶▶▶ [{}] 고객정보 식제", custID);

        // 수입, 경비 데이터 삭제
        log.debug("▶▶▶▶▶▶ 수입정보 식제 건수 : {} 건", bookService.deleteBookIncomeByCustId(custID));
        log.debug("▶▶▶▶▶▶ 경비정보 식제 건수 : {} 건", bookService.deleteBookOutgoingByCustId(custID));
        log.debug("▶▶▶▶▶▶ 마이데이터 수입정보 식제 건수 : {} 건", mydataService.deleteMydataIncomeByCustId(custID));
        log.debug("▶▶▶▶▶▶ 마이데이터 경비정보 식제 건수 : {} 건", mydataService.deleteMydataOutgoingByCustId(custID));
        log.debug("▶▶▶▶▶▶ 전체 수입정보 식제 건수 : {} 건", totalService.deleteTotalIncomeByCustId(custID));
        log.debug("▶▶▶▶▶▶ 전체 경비정보 식제 건수 : {} 건", totalService.deleteTotalOutgoingByCustId(custID));

        // 로그인 이력 삭제
        log.debug("▶▶▶▶▶▶ 로그인이력 식제 건수 : {} 건", loginService.deleteLoginHstByCustId(custID));

        // 알람 데이터 삭제
        noticeService.deleteNotiTargetById(custID);
        log.debug("▶▶▶▶▶▶ 알람대상 식제");
        noticeService.deleteNotiSettingById(custID);
        log.debug("▶▶▶▶▶▶ 알람설정 식제");

        // 헬프데스크 데이터 삭제
        log.debug("▶▶▶▶▶▶ 안내데스트 식제 건수 : {} 건", helpdeskService.deleteHelpdeskAnsByCustId(custID));
        log.debug("▶▶▶▶▶▶ 안내데스크 응답 식제 건수 : {} 건", helpdeskService.deleteHelpdeskByCustId(custID));

        // 단말기정보 데이터 삭제
        devService.deleteDevInfoById(custID);
        log.debug("▶▶▶▶▶▶ 단말기정보 식제");

        // 인증정보 데이터 삭제
        log.debug("▶▶▶▶▶▶ 인증정보 식제 건수 : {} 건", authService.deleteAuthByCustId(custID));

        // 고객정보 관련 데이터 삭제
        log.debug("▶▶▶▶▶▶ 연도 별 고객 자산정보 식제 건수 : {} 건", deleteCustDeductByCustId(custID));
        log.debug("▶▶▶▶▶▶ 고객가족정보 식제 건수 : {} 건", deleteCustFamilyByCustId(custID));
        log.debug("▶▶▶▶▶▶ 고객약관동의 식제 건수 : {} 건", deleteCustTermsAgmtByCustId(custID));
        deleteCustInfoDtlById(custID);
        log.debug("▶▶▶▶▶▶ 고객상세정보 식제");
        deleteCustInfoById(custID);
        log.debug("▶▶▶▶▶▶ 고객정보 식제");
        deleteCustById(custID);
        log.debug("▶▶▶▶▶▶ 고객 식제");
    }
}
