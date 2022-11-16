package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.HashMap;


@Slf4j
public class HealthCheckJob extends BaseJob {

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		log.info("============= HealthCheck QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());
		String targetUrl = "http://localhost:8080/actuator/healthcheck";

		sendReqGETJson(targetUrl, new HashMap<>());

		log.info("============= HealthCheck QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}

	/**
	 * URL GET 요청
	 * @param targetUrl
	 * @param headerMap
	 * @return
	 */
	public void sendReqGETJson(String targetUrl, HashMap<String, String> headerMap) {
		String returnMsg = "";

		if (targetUrl == null || targetUrl.trim() == "") {
			log.info("==== 요청된 URL이 없습니다. ====");
			return;
		}

		//http client 생성
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {

			HttpGet httpGet = new HttpGet(targetUrl);

			// 헤더값 설정
			httpGet.addHeader("Content-Type", "Application/json; charset=UTF-8");
			for (String key : headerMap.keySet()) {
				httpGet.addHeader(key, headerMap.get(key));
			}

			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

			log.info("1. 요청 URL[{}]", targetUrl);
			log.info("==== 요청 처리 결과 리턴 [{}]: \r\n[{}]", httpResponse.getStatusLine().getStatusCode(), targetUrl);

			try {
				// 데이터 수신
				if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "utf-8"));
					// 리턴되는 메시지를 읽는다 .

					String strLine ="";
					while ((strLine=in.readLine())!= null) {
						returnMsg+=strLine;
					}
				} else {
					returnMsg = "전송 실패("+httpResponse.getStatusLine().getStatusCode()+")";
				}
			} catch (ConnectException ex) {
				returnMsg = "[" + httpResponse + "] 연결 실패("+httpResponse.getStatusLine().getStatusCode()+")";
			}

		} catch (Exception e) {
			log.error("**** 요청 처리 실패 {}", e);
		} finally {
			try	{
				// 연결 종료
				httpClient.close();
			} catch(Exception e) {
				log.error("**** 연결 종료 실패 {}", e);
			}
		}

		log.info("2. 요청 처리 결과 메시지 [{}]", returnMsg);
	}
}
