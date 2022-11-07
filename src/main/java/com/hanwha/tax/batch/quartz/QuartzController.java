package com.hanwha.tax.batch.quartz;

import com.hanwha.tax.batch.HttpUtil;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
public class QuartzController {

    @Autowired
    SchedulerFactoryBean sfb;

    @Autowired
    TotalService totalService;

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

    @RequestMapping(value = "/checkAmount", method = RequestMethod.GET)
    public String checkAmount(@RequestParam(name = "cid", required = true) String cid
            , HttpServletRequest req) throws ParseException {

        log.info("## QuartzController.java [checkAmount] Starts..");

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("User-Agent","1.0;iPhone;IOS;16.0.1");
        headerMap.put("uid","635EC356-2AAD-4BD5-A4E6-89F2EACB5546");
        headerMap.put("cid",cid);
        headerMap.put("jwt","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMjA5NDQzNDU4IiwiaWF0IjoxNjYzMTUyMTgzLCJleHAiOjE2NjMxNTU3ODN9.tYFdplB34-Vmy9OrFn6ZiIpnUqgDiwKAsjK4Oo4i5j8");

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
                    int year = (int) jobjInfo.get("year");
                    int month = (int) jobjInfo.get("month");
                    String tyle = (String) jobjInfo.get("tyle");// 1 : 3.3% 포함, 0 : 미포함
                    String total = (String) jobjInfo.get("total");
                    String count = (String) jobjInfo.get("count");

                    Map<String, String> incomeMap = totalService.getTotalIncomeByMonth(cid, year, month, "1".equals(tyle) ? 'Y' : 'N');

                    log.debug("★★★ 금액 [totalApi={}, totalIncome={}]", total, incomeMap.get("total"));
                    if (!total.equals(incomeMap.get("total"))) {
                        log.error("▶︎▶︎▶︎ TOTAL_INCOME 금액을 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalIncome={}]", cid, year, month, tyle, total, incomeMap.get("total"));
                    }
                    log.debug("★★★︎ 건수 [totalApi={}, totalIncome={}]", count, incomeMap.get("count"));
                    if (!count.equals(incomeMap.get("count"))) {
                        log.error("▶︎▶︎▶︎ TOTAL_INCOME 건수를 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalIncome={}]", cid, year, month, tyle, count, incomeMap.get("count"));
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
                    int year = (int) jobjInfo.get("year");
                    int month = (int) jobjInfo.get("month");
                    String category = (String) jobjInfo.get("category");
                    String total = (String) jobjInfo.get("total");
                    String count = (String) jobjInfo.get("count");

                    Map<String, String> outgoingMap = totalService.getTotalOutgoingByMonth(cid, year, month, category);

                    log.debug("★★★ 금액 [totalApi={}, totalOutgoing={}]", total, outgoingMap.get("total"));
                    if (!total.equals(outgoingMap.get("total"))) {
                        log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 금액을 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalIncome={}]", cid, year, month, category, total, outgoingMap.get("total"));
                    }
                    log.debug("★★★︎ 건수 [totalApi={}, totalIncome={}]", count, outgoingMap.get("count"));
                    if (!count.equals(outgoingMap.get("count"))) {
                        log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 건수를 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalIncome={}]", cid, year, month, category, count, outgoingMap.get("count"));
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        log.info("## QuartzController.java [checkAmount] End..");
        return "";
    }
}
