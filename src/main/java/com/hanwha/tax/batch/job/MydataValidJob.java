package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.HttpUtil;
import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class MydataValidJob extends AbstractBaseJob {

	private TotalService totalService;

	private CustService custService;

	@Value("${tax.api.domain}")
	private String domainApi;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		totalService = (TotalService) SpringApplicationContext.getBean("totalService");
		custService = (CustService) SpringApplicationContext.getBean("custService");

		log.info("============= 쿠콘 마이데이터 합계 금액 검증 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		HashMap<String, String> headerMap = new HashMap<>();
		headerMap.put("User-Agent","1.0;iPhone;IOS;16.0.1");
		headerMap.put("uid","thkim0740");
		headerMap.put("jwt","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGtpbTA3NDAiLCJwaW4iOiIxMjM0IiwiaWF0IjoxNjY4NDA5MzI1LCJleHAiOjE2OTk5NDUzMjV9.urADlxbD-gblm1LUJedbfiTsFbA0WzPt_jhgJaNcbHQ");

		// 정상 상태의 정회원 리스트 조회
		custService.getCustListByStatusGrade(Cust.CustStatus.정상.getCode(), Cust.CustGrade.정회원.getCode()).forEach(c -> {
			log.info("▶︎▶︎▶︎ TOTAL 데이터 검증 [{}]", c.getCustId());
			headerMap.put("cid",c.getCustId());

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
						String tyle = (String) jobjInfo.get("tyle");// 1 : 3.3% 포함, 0 : 미포함
						long total = (long) jobjInfo.get("total");
						long count = (long) jobjInfo.get("count");

						Map<String, String> incomeMap = totalService.getTotalIncomeByMonth(c.getCustId(), year, month, "1".equals(tyle) ? 'Y' : 'N');
						long inTotal = "null".equals(String.valueOf(incomeMap.get("total"))) ? 0 : Long.parseLong(String.valueOf(incomeMap.get("total")));
						inTotal = "0".equals(tyle) ? inTotal*1000/967 : inTotal;
						long inCount = "null".equals(String.valueOf(incomeMap.get("count"))) ? 0 : Long.parseLong(String.valueOf(incomeMap.get("count")));

						if (total != inTotal) {
							log.error("▶︎▶︎▶︎ TOTAL_INCOME 금액을 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalIncome={}]", c.getCustId(), year, month, tyle, total, inTotal);
						}
						if (count != inCount) {
							log.error("▶︎▶︎▶︎ TOTAL_INCOME 건수를 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalIncome={}]", c.getCustId(), year, month, tyle, count, inCount);
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
						String category = (String) jobjInfo.get("category");
						long total = (long) jobjInfo.get("total");
						long count = (long) jobjInfo.get("count");

						// 경비제외는 검증하지 않는다.
						if (!MydataOutgoing.CardCategory.경비제외.getCode().equals(category)) {
							Map<String, String> outgoingMap = totalService.getTotalOutgoingByMonth(c.getCustId(), year, month, Utils.lpadByte(category,2,"0"));
							long outTotal = "null".equals(String.valueOf(outgoingMap.get("total"))) ? 0 : Long.parseLong(String.valueOf(outgoingMap.get("total")));
							long outCount = "null".equals(String.valueOf(outgoingMap.get("count"))) ? 0 : Long.parseLong(String.valueOf(outgoingMap.get("count")));

							if (total != outTotal) {
								log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 금액을 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalOutgoing={}]", c.getCustId(), year, month, category, total, outTotal);
							}
							if (count != outCount) {
								log.error("▶︎▶︎▶︎ TOTAL_OUTGOING 건수를 확인해 주시기 바랍니다. [{}][{}][{}][{}][totalApi={}, totalOutgoing={}]", c.getCustId(), year, month, category, count, outCount);
							}
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});


		log.info("============= 쿠콘 마이데이터 합계 금액 검증 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
