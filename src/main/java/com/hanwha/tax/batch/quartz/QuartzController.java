package com.hanwha.tax.batch.quartz;

import com.hanwha.tax.batch.mydata.model.Crypto;
import com.hanwha.tax.batch.HttpUtil;
import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.repository.AuthInfoRepository;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.AuthInfo;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.entity.Tax;
import com.hanwha.tax.batch.mydata.model.GuCrypto;
import com.hanwha.tax.batch.mydata.repository.MydataIncomeRepository;
import com.hanwha.tax.batch.mydata.service.MydataService;
import com.hanwha.tax.batch.tax.service.CalcTax;
import com.hanwha.tax.batch.tax.service.TaxService;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class QuartzController {

    @Autowired
    SchedulerFactoryBean sfb;

    @Autowired
    CustService custService;

    @Autowired
    MydataService mydataService;

    @Autowired
    MydataIncomeRepository mydataIncomeRepository;

    @Autowired
    TotalService totalService;

    @Autowired
    TaxService taxService;

    @Autowired
    CalcTax calcTax;

    @Autowired
    AuthService authService;
    @Autowired
    AuthInfoRepository authInfoRepository;

    @Value("${tax.api.domain}")
    private String domainApi;

    private final Crypto crypto;
    private final GuCrypto guCrypto;

    @RequestMapping(value = "/manualExe", method = RequestMethod.GET)
    public String manualExe(@RequestParam String job
            , @RequestParam(value = "jobGroup", required = false, defaultValue = "DEFAULT") String jobGroup
            , HttpServletRequest req) {

        log.info("## QuartzController.java [manualExe] Starts..");
        String lRet = "";

        try {
            JobKey jobKey = new JobKey(job, jobGroup);
            Scheduler lScheduler = sfb.getScheduler();
            lScheduler.triggerJob(jobKey);
            lRet = "OK: "+ job;

        } catch (Exception e) {
            lRet = "NOK: Exception, " + e.getMessage();
        } finally {
            log.info("## QuartzController.java [manualExe] "+ lRet);
        }

        return lRet;
    }

    @RequestMapping(value = "/saveMydata", method = RequestMethod.GET)
    public String saveMydata(@RequestParam String ymd, HttpServletRequest req) {

        log.info("## QuartzController.java [saveMydata] Starts");

        // 원본이력 데이터 삭제
        log.info("▶︎▶︎▶ 마이데이터 원본 삭제 [{}]", ymd);
        mydataService.resetMydata(ymd);

        log.info("▶︎▶︎▶ 마이데이터 정보 등록 [{}]", ymd);
        mydataService.procMydataJob(ymd);

//        log.info("▶︎▶︎▶ 총 수입/지출 금액 계산 [{}]", ymd);
//        log.info("▶︎▶︎▶ 소득세 계산 [{}]", ymd);
        
        log.info("## QuartzController.java [saveMydata] End");

        return "";
    }

    @RequestMapping(value = "/totalMydata", method = RequestMethod.GET)
    public String totalMydata(@RequestParam String ymd, HttpServletRequest req) {

        log.info("## QuartzController.java [totalMydata] Starts");

        // 기준일 별 총 수입/지출 금액 계산
        log.info("▶▶▶ 총 수입/지출 금액 조회 : [{}]", ymd);
        totalService.procTotalAmountJob(ymd);

        log.info("## QuartzController.java [totalMydata] End");

        return "";
    }

    /**
     * 고객 별 소득세 계산하여 저장
     * @param custId
     * @param yearStr
     */
    private void procTax(String custId, String yearStr) {
        int year = Integer.parseInt(yearStr);

        log.info("▶︎▶︎▶︎ 회원번호 : [{}] 연도 : [{}]", custId, year);

        // 경비율, 간편장부 소득세 계산하여 소득세 저장
        taxService.saveTax(custId, year, calcTax);
    }

    @RequestMapping(value = "/taxAllmembers", method = RequestMethod.GET)
    public String taxAllmembers(@RequestParam int year
            , @RequestParam(value = "ymd", required = false, defaultValue = "") String ymd
            , HttpServletRequest req) {

        log.info("## QuartzController.java [taxAllmembers] Starts");

        if (Utils.isEmpty(ymd)) {
            // 정상 상태의 전체 고객리스트 조회
            custService.getCustListByStatusGrade(Cust.CustStatus.정상.getCode(), Cust.CustGrade.정회원.getCode()).forEach(c -> {
                // 해당 고객의 연도 별 수입이력 조회
                totalService.getTotalIncomeByCustId(c.getCustId()).forEach(t -> {
                    // 소득세 계산 및 저장
                    procTax(t.getCustId(), String.valueOf(t.getYear()));
                });
            });
        } else {
            // 전체 수입/지출 변경이력 조회 ( 수입/지출 뿐만 아니라 ★★★직종이 변경되는 경우 소득세 결과가 달라질 수 있음 )
            totalService.getTotalChangeList(ymd).forEach(t -> {
                // 소득세 계산 및 저장
                procTax(t.get("cust_id"), String.valueOf(t.get("year")));
            });
        }


        log.info("## QuartzController.java [taxAllmembers] End");

        return "";
    }

    private void calcTaxByCustId(String custId, int year) {
        Tax tax = new Tax();
        tax.setCustId(custId);
        tax.setYear(year);

        // 경비율 기반 소득세 및 공제금액 세팅
        calcTax.init(custId, year);
        tax.setRateTax(calcTax.calRateTax());
        tax.setIncome(calcTax.getIncome());
        tax.setRateOutgo(calcTax.getOutgoing());
        tax.setRateMyDeduct(calcTax.getDeductMe());
        tax.setRateFamilyDeduct(calcTax.getDeductFamily());
        tax.setRateOtherDeduct(calcTax.getDeductOthers());
        tax.setRateIraDeduct(calcTax.getTaxDeductIra());

        // 간편장부 기반 소득세 및 공제금액 세팅
        calcTax.init(custId, year);
        tax.setBookTax(calcTax.calBookTax());
        tax.setBookOutgo(calcTax.getOutgoing());
        tax.setBookMyDeduct(calcTax.getDeductMe());
        tax.setBookFamilyDeduct(calcTax.getDeductFamily());
        tax.setBookOtherDeduct(calcTax.getDeductOthers());
        tax.setBookIraDeduct(calcTax.getTaxDeductIra());

        log.info("★★★ tax : [{}]", tax);
    }

    @RequestMapping(value = "/calcTax", method = RequestMethod.GET)
    public String calcTax(@RequestParam(value = "cid", required = false, defaultValue = "") String cid
            , @RequestParam int year
            , HttpServletRequest req) {

        log.info("## QuartzController.java [calcTax] Starts");

        // 고객번호가 비어있는 경우 정상 상태의 전체 고객리스트 조회
        if (Utils.isEmpty(cid)) {
            custService.getCustListByStatusGrade(Cust.CustStatus.정상.getCode(), Cust.CustGrade.정회원.getCode()).forEach(c -> {
                calcTaxByCustId(c.getCustId(), year);
            });
        } else {
            calcTaxByCustId(cid, year);
        }

        log.info("## QuartzController.java [calcTax] End");

        return "";
    }

    @RequestMapping(value = "/validMydata", method = RequestMethod.GET)
    public String validMydata(@RequestParam String cid, HttpServletRequest req) throws ParseException {

        log.info("## QuartzController.java [validMydata] Starts");

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("User-Agent","1.0;iPhone;IOS;16.0.1");
        headerMap.put("uid","thkim0740");
        headerMap.put("cid",cid);
        headerMap.put("jwt","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGtpbTA3NDAiLCJwaW4iOiIxMjM0IiwiaWF0IjoxNjY4NDA5MzI1LCJleHAiOjE2OTk5NDUzMjV9.urADlxbD-gblm1LUJedbfiTsFbA0WzPt_jhgJaNcbHQ");

        // 수입정보 요청
        String returnIncome = HttpUtil.sendReqGETJson(domainApi+"/api/v1/mydata/ccIncome", headerMap);
        // 수입정보 분석
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jobjResult = (JSONObject) jsonParser.parse(returnIncome);
            JSONObject jobjData = (JSONObject) jobjResult.get("data");

            if ("00000".equals(jobjData.get("rsp_code"))) {
                JSONArray jArrList = (JSONArray) jobjData.get("incomes_list");

                for (int i = 0; i < jArrList.size(); i++) {
                    JSONObject jobjInfo = (JSONObject) jArrList.get(i);
                    long year = (long) jobjInfo.get("year");
                    long month = (long) jobjInfo.get("month");
                    char is33 = "1".equals(String.valueOf(jobjInfo.get("tyle"))) ? 'Y' : 'N';// 1 : 3.3% 포함, 0 : 미포함
                    long total = (long) jobjInfo.get("total");
                    long count = (long) jobjInfo.get("count");

                    Map<String, String> incomeMap = totalService.getTotalIncomeByMonth(cid, year, month, is33);
                    long inTotal = "null".equals(String.valueOf(incomeMap.get("total"))) ? 0 : Long.parseLong(String.valueOf(incomeMap.get("total")));
                    long inCount = "null".equals(String.valueOf(incomeMap.get("count"))) ? 0 : Long.parseLong(String.valueOf(incomeMap.get("count")));
                    total = (is33 == 'N') ? total*1000/967 : total;

                    if (total != inTotal) {
                        log.error("▶︎▶︎▶︎ TOTAL_INCOME 금액을 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and is_33='{}'][totalApi={}, totalIncome={}]", cid, year, month, is33, total, inTotal);
                    }
                    if (count != inCount) {
                        log.error("▶︎▶︎▶︎ TOTAL_INCOME 건수를 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and is_33='{}'][totalApi={}, totalIncome={}]", cid, year, month, is33, count, inCount);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 경비정보 요청
        String returnOutgoing = HttpUtil.sendReqGETJson(domainApi+"/api/v1/mydata/ccExpense", headerMap);
        // 경비정보 분석
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jobjResult = (JSONObject) jsonParser.parse(returnOutgoing);
            JSONObject jobjData = (JSONObject) jobjResult.get("data");

            if ("00000".equals(jobjData.get("rsp_code"))) {
                JSONArray jArrList = (JSONArray) jobjData.get("expense_list");

                for (int i = 0; i < jArrList.size(); i++) {
                    JSONObject jobjInfo = (JSONObject) jArrList.get(i);
                    long year = (long) jobjInfo.get("year");
                    long month = (long) jobjInfo.get("month");
                    String category = Utils.lpadByte(String.valueOf(jobjInfo.get("category")),2,"0");
                    long total = (long) jobjInfo.get("total");
                    long count = (long) jobjInfo.get("count");

                    // 경비제외는 검증하지 않는다.
                    if (!MydataOutgoing.CardCategory.경비제외.getCode().equals(category)) {
                        Map<String, String> outgoingMap = totalService.getTotalOutgoingByMonth(cid, year, month, category);
                        long outTotal = "null".equals(String.valueOf(outgoingMap.get("total"))) ? 0 : Long.parseLong(String.valueOf(outgoingMap.get("total")));
                        long outCount = "null".equals(String.valueOf(outgoingMap.get("count"))) ? 0 : Long.parseLong(String.valueOf(outgoingMap.get("count")));

                        if (total != outTotal) {
                            log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 금액을 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and category='{}'][totalApi={}, totalIncome={}]", cid, year, month, category, total, outTotal);
                        }
                        if (count != outCount) {
                            log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 건수를 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and category='{}'][totalApi={}, totalIncome={}]", cid, year, month, category, count, outCount);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        log.info("## QuartzController.java [validMydata] End");
        return "";
    }

    @RequestMapping(value = "/selectMydataIncome", method = RequestMethod.GET)
    public String selectMydataIncome(@RequestParam String cid, HttpServletRequest req) {

        log.info("## QuartzController.java [selectMydataIncome] Starts");

        // 마이데이터 수입내역 조회
        mydataService.getMydataIncomeByCustId(cid).forEach(mi -> {
            log.info("[{}]", mi.toString());
        });

        log.info("## QuartzController.java [selectMydataIncome] End");

        return "";
    }

    @RequestMapping(value = "/selectMydataOutgoing", method = RequestMethod.GET)
    public String selectMydataOutgoing(@RequestParam String cid, HttpServletRequest req) {

        log.info("## QuartzController.java [selectMydataOutgoing] Starts");

        // 마이데이터 경비내역 조회
        mydataService.getMydataOutgoingByCustId(cid).forEach(mo -> {
            log.info("[{}]", mo.toString());
        });

        log.info("## QuartzController.java [selectMydataOutgoing] End");

        return "";
    }

    @RequestMapping(value = "/selectAuth", method = RequestMethod.GET)
    public String selectAuth(@RequestParam String cid, HttpServletRequest req) {

        log.info("## QuartzController.java [selectAuth] Starts");

        // 고객번호로 인증정보 조회
        log.info("[{}]", authService.getAuthInfo(cid).toString());

        log.info("## QuartzController.java [selectAuth] End");

        return "";
    }

    @RequestMapping(value = "/selectTax", method = RequestMethod.GET)
    public String selectTax(@RequestParam String cid, @RequestParam int year, HttpServletRequest req) {

        log.info("## QuartzController.java [selectTax] Starts");

        // 소득세정보 조회
        log.info("[{}]", taxService.getTax(cid, year).orElse(null));

        log.info("## QuartzController.java [selectTax] End");

        return "";
    }

    @RequestMapping(value = "/encryptMydata", method = RequestMethod.GET)
    public String encryptMydata(@RequestParam(value = "cid", required = false, defaultValue = "") String cid, HttpServletRequest req) {

        log.info("## QuartzController.java [encryptMydata] Starts");

        // 마이데이터 수입정보 암호화
        if (Utils.isEmpty(cid)) {
            mydataService.getMydataIncomeList().forEach(mi -> {
                mi.setAccountNum(crypto.encodeAESCBC(mi.getAccountNum()));
                mydataIncomeRepository.save(mi);
            });
        } else {
            mydataService.getMydataIncomeByCustId(cid).forEach(mi -> {
                mi.setAccountNum(crypto.encodeAESCBC(mi.getAccountNum()));
                mydataIncomeRepository.save(mi);
            });
        }

        log.info("## QuartzController.java [encryptMydata] End");

        return "";
    }

    @RequestMapping(value = "/decryptMydata", method = RequestMethod.GET)
    public String decryptMydata(@RequestParam(value = "cid", required = false, defaultValue = "") String cid, HttpServletRequest req) {

        log.info("## QuartzController.java [decryptMydata] Starts");

        // 마이데이터 수입정보 복호화
        if (Utils.isEmpty(cid)) {
            mydataService.getMydataIncomeList().forEach(mi -> {
                mi.setAccountNum(crypto.decodeAESCBC(mi.getAccountNum()));
                mydataIncomeRepository.save(mi);
            });
        } else {
            mydataService.getMydataIncomeByCustId(cid).forEach(mi -> {
                mi.setAccountNum(crypto.decodeAESCBC(mi.getAccountNum()));
                mydataIncomeRepository.save(mi);
            });
        }

        log.info("## QuartzController.java [decryptMydata] End");

        return "";
    }

    @RequestMapping(value = "/guDecryptMydata", method = RequestMethod.GET)
    public String guDecryptMydata(@RequestParam(value = "cid", required = false, defaultValue = "") String cid, HttpServletRequest req) {

        log.info("## QuartzController.java [guDecryptMydata] Starts");

        // 마이데이터 수입정보 구복호화
        if (Utils.isEmpty(cid)) {
            mydataService.getMydataIncomeList().forEach(mi -> {
                mi.setAccountNum(guCrypto.decodeAESCBC(mi.getAccountNum()));
                mydataIncomeRepository.save(mi);
            });
        } else {
            mydataService.getMydataIncomeByCustId(cid).forEach(mi -> {
                mi.setAccountNum(guCrypto.decodeAESCBC(mi.getAccountNum()));
                mydataIncomeRepository.save(mi);
            });
        }

        log.info("## QuartzController.java [guDecryptMydata] End");

        return "";
    }

    @RequestMapping(value = "/revokeMydataByCi", method = RequestMethod.GET)
    public String revokeMydataByCi(@RequestParam String ci, HttpServletRequest req) {

        log.info("## QuartzController.java [revokeMydataByCi] Starts");

        ci = ci.replace("%26","&").replace("%2B","+").replace("%3D","=");
        log.info("▶▶▶ 제3자 제공동의 철회 : [{}]", ci);

        AuthInfo auth = new AuthInfo();
        auth.setCustId("cid"+ci.substring(0,7));
        auth.setAuthType("PIN");
        auth.setPin("pin"+ci.substring(7,24));
        auth.setCi(ci);
        auth.setCreateDt(Utils.getCurrentDateTime());

        // 인증정보 임의 저장
        authInfoRepository.save(auth);

        // 제3자 제공동의 철회
        mydataService.revoke(auth.getCustId());

        // 3초간 멈춤
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("**** 요청 처리 실패 {}", e);
        }

        // 인증정보 삭제
        authInfoRepository.delete(auth);

        log.info("## QuartzController.java [revokeMydataByCi] End");

        return "";
    }

    @RequestMapping(value = "/saveCustStat", method = RequestMethod.GET)
    public String saveCustStat(@RequestParam String ymd, HttpServletRequest req) {

        log.info("## QuartzController.java [saveCustStat] Starts");

        // 고객 이용 현황 저장
        custService.saveCustStat(ymd);

        log.info("## QuartzController.java [saveCustStat] End");

        return "";
    }

    @RequestMapping(value = "/selectCustStat")
    public String selectCustStat(@RequestParam String from, @RequestParam String to, HttpServletRequest req) {

        log.info("## QuartzController.java [selectCustStat] Starts");
        log.info("▶▶▶ 회원 이용 현황 조회 : [{} ~ {}]", from, to);

        // 고객 이용 현황 조회
        custService.getCustStatList(from, to).forEach(cs -> {
            log.info("[{}]", cs.toString());
        });

        log.info("## QuartzController.java [selectCustStat] End");

        return "";
    }

    @RequestMapping(value = "/selectLstCustStatHst")
    public String selectLstCustStatHst(@RequestParam String ymd, HttpServletRequest req) {

        log.info("## QuartzController.java [selectLstCustStatHst] Starts");
        log.info("▶▶▶ 회원 마지막 이용 현황 조회 : [{}]", ymd);

        // 고객 이용 현황 조회
        custService.getLstCustStatHstList(ymd).forEach(lcs -> {
            log.info("[LoginHst(custId={}, loginDt={}, authStatus={})]", lcs.get("cust_id"), lcs.get("login_dt"), lcs.get("auth_status"));
        });

        log.info("## QuartzController.java [selectLstCustStatHst] End");

        return "";
    }
}
