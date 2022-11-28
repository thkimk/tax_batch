package com.hanwha.tax.batch.quartz;

import com.hanwha.tax.batch.HttpUtil;
import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.auth.service.AuthService;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.*;
import com.hanwha.tax.batch.mydata.service.MydataService;
import com.hanwha.tax.batch.tax.service.CalcTax;
import com.hanwha.tax.batch.tax.service.TaxService;
import com.hanwha.tax.batch.total.service.TotalService;
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
import java.util.concurrent.atomic.AtomicLong;

import static com.hanwha.tax.batch.Constants.BANK_FILE;
import static com.hanwha.tax.batch.Constants.CARD_FILE;
import static com.hanwha.tax.batch.Constants.BANK_TRANS_FILE;
import static com.hanwha.tax.batch.Constants.CARD_APPR_FILE;
import static com.hanwha.tax.batch.Constants.THIRDPARTY_FILE;

@Slf4j
@Controller
public class QuartzController {

    @Autowired
    SchedulerFactoryBean sfb;

    @Autowired
    CustService custService;

    @Autowired
    MydataService mydataService;

    @Autowired
    TotalService totalService;

    @Autowired
    TaxService taxService;

    @Autowired
    CalcTax calcTax;

    @Autowired
    AuthService authService;

    @Value("${tax.api.domain}")
    private String domainApi;

    @RequestMapping(value = "/manualExe", method = RequestMethod.GET)
    public String manualExe(@RequestParam(name = "job", required = true) String job
            , @RequestParam(name = "jobGroup", required = false, defaultValue = "DEFAULT") String jobGroup
            , HttpServletRequest req) {

        log.info("## QuartzController.java [manualExe] Starts..");
        String lRet = "";
//        List<Map<String, Object>> jobList = selectJobs();

        try {

//            // Job 실행 제약 체크
//            String checkJob = checkJob(job);
//            if(!checkJob.isEmpty()) {
//                throw new BizException(checkJob);
//            }
//
//            Optional<Map<String, Object>> tempList = jobList.stream()
//                    .filter(tempMap -> tempMap.get("jobName").toString().equals(job))
//                    .findFirst();
//
//            if(!tempList.isPresent()) {
//                lRet = "## don't find [jobName] : " + job + ", " + " [jobGroup] : " + jobGroup + "\n";
//                lRet += "============== possibility job \n";
//                for(int i=0;i<jobList.size();i++) {
//                    lRet += "[jobName] : " + jobList.get(i).get("jobName") + ", "
//                            + "[jobGroup] : " + jobList.get(i).get("jobGroup") + "\n";
//                }
//                lRet += "==============";
//                return lRet;
//            }

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
    public String saveMydata(@RequestParam(name = "ymd", required = true) String ymd
            , HttpServletRequest req) {

        log.info("## QuartzController.java [saveMydata] Starts");

        // 원본이력 데이터 삭제
        log.info("▶︎▶︎▶ 마이데이터 원본 삭제 [{}]", ymd);
        mydataService.resetMydata(ymd);

        log.info("▶︎▶︎▶ 마이데이터 은행(원본) 확인 [{}]",ymd);
        mydataService.procMydataInfo(BANK_FILE, ymd);			// 은행(원본) 파일 확인
        log.info("▶︎▶︎▶ 마이데이터 카드(원본) 확인 [{}]",ymd);
        mydataService.procMydataInfo(CARD_FILE, ymd);			// 카드(원본) 파일 확인

        log.info("▶︎▶︎▶ 마이데이터 은행(수입) 확인 [{}]",ymd);
        mydataService.procMydataInfo(BANK_TRANS_FILE, ymd);	// 은행(수입) 파일 확인
        log.info("▶︎▶︎▶ 마이데이터 카드(경비) 확인 [{}]",ymd);
        mydataService.procMydataInfo(CARD_APPR_FILE, ymd);	// 카드(경비) 파일 확인

        log.info("▶︎▶︎▶ 마이데이터 제3자 제공동의 확인 [{}]",ymd);
        mydataService.procMydataInfo(THIRDPARTY_FILE, ymd);	// 제3자 제공동의 파일 확인

        log.info("## QuartzController.java [saveMydata] End");

        return "";
    }

    @RequestMapping(value = "/totalMydata", method = RequestMethod.GET)
    public String totalMydata(@RequestParam(name = "ymd", required = true) String ymd
            , HttpServletRequest req) {

        log.info("## QuartzController.java [totalMydata] Starts");

        // 간편장부, 마이데이터 수입 변경내역 조회
        log.info("▶▶▶ TOTAL 수입정보 저장");
        totalService.getTotalIncomeTarget(ymd).forEach(ti -> {
            // 전체수입정보
            TotalIncome totalIncome = new TotalIncome().convertByMydataMap(ti);

            // 총 수입금액 저장 ( 수입여부가 'Y'인 경우만 저장/'N'인 경우 삭제 )
            if ("Y".equals(ti.get("is_income"))) {
                totalService.saveTotalIncome(totalIncome);
            } else {
                totalService.deleteTotalIncomeByFkAndFlagFk(totalIncome.getFk(), totalIncome.getFlagFk());
            }
        });

        // 간편장부, 마이데이터 지출 변경내역 조회
        log.info("▶▶▶ TOTAL 지출정보 저장");
        totalService.getTotalOutgoingTarget(ymd).forEach(to -> {
            // 전체지출정보
            TotalOutgoing totalOutgoing = new TotalOutgoing().convertByMydataMap(to);

            // 마이데이터 지출정보인 경우만 금액 계산
            if (!Utils.isEmpty(to.get("appr_num"))) {
                // 지출금액
                AtomicLong amount = new AtomicLong();

                // 마이데이터 승인번호 별 카드이력 조회
                mydataService.getMydataOutgoingByCardInfo(to.get("org_code"), to.get("card_id"), to.get("appr_num")).forEach(mo -> {
                    // ★★★ 테스트 데이터 중복되어 코드 추가 함
                    if (!mo.getCustId().equals(to.get("cust_id")))
                        return;

                    // 경비제외가 아닌 경우 지출금액 계산 ( ★★★ 원본 데이터 삽입 시 경비코드 빈값으로 셋팅됨 )
                    if (Utils.isEmpty(mo.getCategory()) || MydataOutgoing.CardCategory.경비제외.getCode().equals(mo.getCategory()))
                        return;

                    // 승인, 승인취소, 정정에 따른 지출금액 계산
                    if (MydataOutgoing.ApprStatus.승인.getCode().equals(mo.getStatus())) {
                        amount.set(amount.get()+mo.getApprAmt());

                        // 최초 승인내역 기준으로 외래키 세팅
                        totalOutgoing.setFk(totalOutgoing.getFk() < mo.getId() ? totalOutgoing.getFk() : mo.getId());
                    } else if (MydataOutgoing.ApprStatus.승인취소.getCode().equals(mo.getStatus())) {
                        amount.set(amount.get()-mo.getApprAmt());
                    } else {
                        amount.set(mo.getModAmt());
                    }
                });

                // 총 지출금액 세팅
                totalOutgoing.setAmount(amount.get());
            }

            // 총 지출금액 저장 ( 금액이 있는 경우만 저장/갱신하고 0원인 경우 삭제 )
            if (0 < totalOutgoing.getAmount()) {
                totalService.saveTotalOutgoing(totalOutgoing);
            } else {
                totalService.deleteTotalOutgoingByFkAndFlagFk(totalOutgoing.getFk(), totalOutgoing.getFlagFk());
            }
        });

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
    public String taxAllmembers(@RequestParam(name = "year", required = true) int year
            , @RequestParam(name = "ymd", required = false, defaultValue = "") String ymd
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
    public String calcTax(@RequestParam(name = "cid", required = false, defaultValue = "") String cid
            , @RequestParam(name = "year", required = true) int year
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
    public String validMydata(@RequestParam(name = "cid", required = true) String cid
            , HttpServletRequest req) throws ParseException {

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

                    log.debug("★★★ 금액 [totalApi={}, totalIncome={}]", total, incomeMap.get("total"));
                    if (total != inTotal) {
                        log.error("▶︎▶︎▶︎ TOTAL_INCOME 금액을 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and is_33='{}'][totalApi={}, totalIncome={}]", cid, year, month, is33, total, incomeMap.get("total"));
                    }
                    log.debug("★★★︎ 건수 [totalApi={}, totalIncome={}]", count, incomeMap.get("count"));
                    if (count != inCount) {
                        log.error("▶︎▶︎▶︎ TOTAL_INCOME 건수를 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and is_33='{}'][totalApi={}, totalIncome={}]", cid, year, month, is33, count, incomeMap.get("count"));
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

                        log.debug("★★★ 금액 [totalApi={}, totalOutgoing={}]", total, outgoingMap.get("total"));
                        if (total != outTotal) {
                            log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 금액을 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and category='{}'][totalApi={}, totalIncome={}]", cid, year, month, category, total, outgoingMap.get("total"));
                        }
                        log.debug("★★★︎ 건수 [totalApi={}, totalIncome={}]", count, outgoingMap.get("count"));
                        if (count != outCount) {
                            log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 건수를 확인해 주시기 바랍니다. [cust_id='{}' and year={} and month={} and category='{}'][totalApi={}, totalIncome={}]", cid, year, month, category, count, outgoingMap.get("count"));
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
    public String selectMydataIncome(@RequestParam(name = "cid", required = true) String cid
            , HttpServletRequest req) {

        log.info("## QuartzController.java [selectMydataIncome] Starts");

        // 마이데이터 수입내역 조회
        mydataService.getMydataIncomeByCustId(cid).forEach(mi -> {
            log.info("[{}]", mi.toString());
        });

        log.info("## QuartzController.java [selectMydataIncome] End");

        return "";
    }

    @RequestMapping(value = "/selectMydataOutgoing", method = RequestMethod.GET)
    public String selectMydataOutgoing(@RequestParam(name = "cid", required = true) String cid
            , HttpServletRequest req) {

        log.info("## QuartzController.java [selectMydataOutgoing] Starts");

        // 마이데이터 경비내역 조회
        mydataService.getMydataOutgoingByCustId(cid).forEach(mo -> {
            log.info("[{}]", mo.toString());
        });

        log.info("## QuartzController.java [selectMydataOutgoing] End");

        return "";
    }

    @RequestMapping(value = "/selectAuth", method = RequestMethod.GET)
    public String selectAuth(@RequestParam(name = "cid", required = true) String cid
            , HttpServletRequest req) {

        log.info("## QuartzController.java [selectAuth] Starts");

        // 고객번호로 인증정보 조회
        authService.getAuthInfoByCustId(cid).forEach(ai -> {
            log.info("[{}]", ai.toString());
        });

        log.info("## QuartzController.java [selectAuth] End");

        return "";
    }
}
