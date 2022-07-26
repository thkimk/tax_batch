package com.hanwha.tax.batch.cust.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.book.service.BookService;
import com.hanwha.tax.batch.cust.model.CustDeductId;
import com.hanwha.tax.batch.cust.repository.CustDeductRepository;
import com.hanwha.tax.batch.cust.repository.CustEventRepository;
import com.hanwha.tax.batch.cust.repository.CustFamilyRepository;
import com.hanwha.tax.batch.cust.repository.CustInfoRepository;
import com.hanwha.tax.batch.cust.repository.CustInfoDtlRepository;
import com.hanwha.tax.batch.cust.repository.CustRepository;
import com.hanwha.tax.batch.cust.repository.CustStatRepository;
import com.hanwha.tax.batch.cust.repository.CustTermsAgmtRepository;
import com.hanwha.tax.batch.dev.service.DevService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.entity.CustDeduct;
import com.hanwha.tax.batch.entity.CustFamily;
import com.hanwha.tax.batch.entity.CustInfo;
import com.hanwha.tax.batch.entity.CustInfoDtl;
import com.hanwha.tax.batch.entity.CustStat;
import com.hanwha.tax.batch.helpdesk.service.HelpdeskService;
import com.hanwha.tax.batch.login.service.LoginService;
import com.hanwha.tax.batch.mydata.service.MydataService;
import com.hanwha.tax.batch.notice.service.NoticeService;
import com.hanwha.tax.batch.tax.service.TaxService;
import com.hanwha.tax.batch.terms.service.TermsService;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    CustEventRepository custEventRepository;

    @Autowired
    CustStatRepository custStatRepository;

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
    TermsService termsService;

    @Autowired
    MydataService mydataService;

    @Autowired
    NoticeService noticeService;

    @Autowired
    TotalService totalService;

    @Autowired
    TaxService taxService;

    /**
     * 고객상세정보의 자산변경일시 갱신
     * @param custId
     * @return
     */
    public CustInfoDtl updateCustMydataDt(String custId) {
        custInfoDtlRepository.findById(custId).ifPresent(cid -> {
            cid.setMydataDt(Utils.getCurrentDateTime());
            custInfoDtlRepository.save(cid);
        });

        return null;
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
     * 고객번호로 고객이벤트 삭제
     * @param custId
     * @return
     */
    public int deleteCustEventByCustId(String custId) {
        return custEventRepository.deleteByCustId(custId);
    }

    /**
     * 고객 데이터 파기
     * @param custID
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void destroyCust(String custID) throws Exception {
        log.info("▶▶▶ [{}] 고객정보 식제 ◀◀◀", custID);
        custRepository.destroyCust(custID);
    }

    /**
     * 고객 데이터 파기
     * @param custID
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustData(String custID) throws Exception {
        log.info("▶▶▶ [{}] 고객정보 식제 시작 ◀◀◀", custID);

        // 수입, 경비 데이터 삭제
        log.info("▶▶▶▶▶▶ 수입정보 식제 건수 : {} 건", bookService.deleteBookIncomeByCustId(custID));
        log.info("▶▶▶▶▶▶ 경비정보 식제 건수 : {} 건", bookService.deleteBookOutgoingByCustId(custID));
        mydataService.deleteMydataByCustId(custID);
        log.info("▶▶▶▶▶▶ 전체 수입정보 식제 건수 : {} 건", totalService.deleteTotalIncomeByCustId(custID));
        log.info("▶▶▶▶▶▶ 전체 경비정보 식제 건수 : {} 건", totalService.deleteTotalOutgoingByCustId(custID));
        log.info("▶▶▶▶▶▶ 소득세정보 식제 건수 : {} 건", taxService.deleteTaxByCustId(custID));

        // 로그인 이력 삭제
        log.info("▶▶▶▶▶▶ 로그인이력 식제 건수 : {} 건", loginService.deleteLoginHstByCustId(custID));
        // 약관동의 이력 삭제
        log.info("▶▶▶▶▶▶ 약관동의이력 식제 건수 : {} 건", termsService.deleteTermsHstByCustId(custID));

        // 알람 데이터 삭제
        noticeService.deleteNotiTargetById(custID);
        log.info("▶▶▶▶▶▶ 알람대상 식제");
        noticeService.deleteNotiSettingById(custID);
        log.info("▶▶▶▶▶▶ 알람설정 식제");

        // 헬프데스크 데이터 삭제
        log.info("▶▶▶▶▶▶ 안내데스트 응답 식제 건수 : {} 건", helpdeskService.deleteHelpdeskAnsByCustId(custID));
        log.info("▶▶▶▶▶▶ 안내데스크 식제 건수 : {} 건", helpdeskService.deleteHelpdeskByCustId(custID));

        // 단말기정보 데이터 삭제
        devService.deleteDevInfoById(custID);
        log.info("▶▶▶▶▶▶ 단말기정보 식제");

        // 인증정보 데이터 삭제
        log.info("▶▶▶▶▶▶ 인증정보 식제 건수 : {} 건", authService.deleteAuthByCustId(custID));

        // 고객정보 관련 데이터 삭제
        log.info("▶▶▶▶▶▶ 연도 별 고객 자산정보 식제 건수 : {} 건", deleteCustDeductByCustId(custID));
        log.info("▶▶▶▶▶▶ 고객가족정보 식제 건수 : {} 건", deleteCustFamilyByCustId(custID));
        log.info("▶▶▶▶▶▶ 고객약관동의 식제 건수 : {} 건", deleteCustTermsAgmtByCustId(custID));
        log.info("▶▶▶▶▶▶ 고객이벤트정보 식제 건수 : {} 건", deleteCustEventByCustId(custID));
        deleteCustInfoDtlById(custID);
        log.info("▶▶▶▶▶▶ 고객상세정보 식제");
        deleteCustInfoById(custID);
        log.info("▶▶▶▶▶▶ 고객기본정보 식제");
        deleteCustById(custID);
        log.info("▶▶▶▶▶▶ 고객마스터 식제");

        log.info("▶▶▶ [{}] 고객정보 식제 종료 ◀◀◀", custID);
    }

    /**
     * 휴면기간에 해당하는 휴면고객 리스트 조회
     * @param ymdBasic
     * @param custStatus
     * @return
     */
    public List<Cust> getCustDormancyListByYmdBasic(String ymdBasic, String custStatus) {
        return custRepository.getCustDormancyList(ymdBasic, custStatus);
    }

    /**
     * 고객정보 변경
     * @param cust
     * @return
     */
    public Cust modifyCust(Cust cust) {
        cust.setUpdateDt(Utils.getCurrentDateTime());
        return custRepository.save(cust);
    }

    /**
     * 고객 휴면전환 배치
     */
    public void custDormancyBatch() {
        // 1년동안 이용 기록이 없는 고객리스트 조회
        List<Cust> custDormancyList = getCustDormancyListByYmdBasic(Utils.addYears(Utils.getCurrentDate("yyyy-MM-dd"),-1)+" 23:59:59", Cust.CustStatus.정상.getCode());
        log.info("▶▶▶ 휴면 대상 고객 건수 : {} 건", custDormancyList.size());

        // 고객 휴면처리
        for (Cust cust : custDormancyList) {
            cust.setCustStatus(Cust.CustStatus.휴면.getCode());
            modifyCust(cust);
        }
    }

    /**
     * 고객리스트 조회
     * @return
     */
    public List<Cust> getCustList() {
        return custRepository.findAll();
    }

    /**
     * 회원 상태 별 회원리스트 조회
     * @param custStatus
     * @return
     */
    public List<Cust> getCustListByStatus(String custStatus) {
        return custRepository.findByCustStatus(custStatus);
    }

    /**
     * 회원 상태/등급 별 회원리스트 조회
     * @param custStatus
     * @return
     */
    public List<Cust> getCustListByStatusGrade(String custStatus, String custGrade) {
        return custRepository.findByCustStatusAndCustGrade(custStatus, custGrade);
    }

    /**
     * 회원 번호로 회원 마스터정보 조회
     *
     * @param custId
     * @return
     */
    public Optional<Cust> getCust(String custId) {
        return custRepository.findById(custId);
    }

    /**
     * 회원 번호로 회원 기본정보 조회
     *
     * @param custId
     * @return
     */
    public Optional<CustInfo> getCustInfo(String custId) {
        return custInfoRepository.findById(custId);
    }

    /**
     * 회원 번호로 회원 상세정보 조회
     * @param custId
     * @return
     */
    public Optional<CustInfoDtl> getCustInfoDtl(String custId) {
        return custInfoDtlRepository.findById(custId);
    }

    /**
     * 회원 번호로 회원 간편장부 조회
     * @param custId
     * @return
     */
    public Optional<CustDeduct> getCustDeduct(String custId, int year) {
        return custDeductRepository.findById(new CustDeductId(custId, year));
    }

    /**
     * 회원 번호로 회원 부양가족 리스트 조회
     * @param custId
     * @return
     */
    public List<CustFamily> getCustFamilyListByCustId(String custId) {
        return custFamilyRepository.findByCustId(custId);
    }

    /**
     * 회원의 직전년도,당해년도 수입 조회
     * @param custId
     * @param year
     * @return
     */
    public Long[] getCustIncomes(String custId, int year) {
        return custDeductRepository.getCustIncomes(custId, year);
    }

    /**
     * 작년도 공제항목 승계
     * @return
     */
    public int successionLastYearDeduct() {
        return custDeductRepository.successionLastYearDeduct();
    }

    /**
     * 고객 이벤트 참여 인원수 조회
     * @param eventId
     * @param result
     * @param joinDt
     * @return
     */
    public int getCntCustEventApply(String eventId, char result, String joinDt) {
        return custEventRepository.countByEventIdAndResultAndJoinDtContains(eventId, result, joinDt);
    }

    /**
     * 기준일별 고객 이용 현황 저장
     * @param basicYmd
     * @return
     */
    public CustStat saveCustStat(String basicYmd) {
        CustStat custStat = new CustStat();
        custStat.setBasicYmd(basicYmd);

        BeanWrapper wrapper = new BeanWrapperImpl(custStat);

        // 기준일 별 등급 현황
        custStatRepository.getCustStatTarget(basicYmd).forEach(cgs -> {
            if (!Utils.isEmpty(cgs.get("col"))) {
                wrapper.setPropertyValue(cgs.get("col"), cgs.get("cnt") == null ? 0 : Integer.parseInt(String.valueOf(cgs.get("cnt"))));
            }
        });
        // 현재 등급 현황
        custStatRepository.getCustStatAccTarget().forEach(cgs -> {
            wrapper.setPropertyValue(cgs.get("col"), cgs.get("cnt") == null ? 0 : Integer.parseInt(String.valueOf(cgs.get("cnt"))));
        });

        return custStatRepository.save(custStat);
    }

    /**
     * from ~ to 일자의 회원 이용 현황 조회
     * @param from
     * @param to
     * @return
     */
    public List<CustStat> getCustStatList(String from, String to) {
        return custStatRepository.findByBasicYmdBetween(from, to);
    }

    /**
     * 기준일별 고객 마지막 이용 이력 조회
     * @param basicYmd
     * @return
     */
    public List<Map<String, String>> getLstCustStatHstList(String basicYmd) {
        return custStatRepository.getLstCustStatHst(basicYmd);
    }
}
