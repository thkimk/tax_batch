package com.hanwha.tax.batch;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;

@Slf4j
public class HttpUtil {
	/**
	 * URL GET 요청
	 * @param targetUrl
	 * @param headerMap
	 * @return
	 */
	public static String sendReqGETJson(String targetUrl, HashMap<String, String> headerMap) {
		String result = Constants.CODE_RET_NOK;
		String returnMsg = "";

		if (Utils.isEmpty(targetUrl)) {
			log.info("==== 요청된 URL이 없습니다. ====");
			return "";
		}

		//http client 생성
		CloseableHttpClient httpClient = HttpClients.createDefault();

		BufferedReader in = null;
		try {

			HttpGet httpGet = new HttpGet(targetUrl);

			// 헤더값 설정
			httpGet.addHeader("Content-Type", "Application/json; charset=UTF-8");
			for (final String key : headerMap.keySet()) {
				httpGet.addHeader(key, headerMap.get(key));
			}

			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

			log.info("1. 요청 URL [{}]", targetUrl);
			log.info("==== 요청 처리 결과 리턴 [{}]: \r\n[{}]", httpResponse.getStatusLine().getStatusCode(), targetUrl);

			// 데이터 수신
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "utf-8"));
				// 리턴되는 메시지를 읽는다 .

				String strLine ="";
				while ((strLine=in.readLine())!= null) {
					returnMsg+=strLine;
				}
				result = Constants.CODE_RET_OK;
			} else {
				returnMsg = "전송 실패("+httpResponse.getStatusLine().getStatusCode()+")";
			}

		} catch (IOException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} finally {
			try {
				// 자원 해제
				if (in != null) {
					in.close();
				}

				// 연결 종료
				httpClient.close();
			} catch (IOException e) {
				log.error("**** 연결 종료 실패 {}", e);
			}
		}

		if (Constants.CODE_RET_NOK.equals(result))
			return "";

		log.info("2. 요청 처리 결과 메시지 [{}]", returnMsg);

		return returnMsg;
	}

	/**
	 * URL POST 요청
	 * @param targetUrl
	 * @param param
	 * @param headerMap
	 * @return
	 */
	public static String sendReqPOSTJson(String targetUrl, String param, HashMap<String, String> headerMap) {
		String result = Constants.CODE_RET_NOK;
		String returnMsg = "";

		if (Utils.isEmpty(targetUrl)) {
			log.info("==== 요청된 URL이 없습니다. ====");
			return "";
		}

		//http client 생성
		CloseableHttpClient httpClient = HttpClients.createDefault();

		BufferedReader in = null;
		try {

			HttpPost httpPost = new HttpPost(targetUrl);
			StringEntity entity = new StringEntity(param, HTTP.UTF_8);
			httpPost.setEntity(entity);

			// 헤더값 설정
			httpPost.addHeader("Content-Type", "Application/json; charset=UTF-8");
			if (headerMap != null) {
				for (final String key : headerMap.keySet()) {
					httpPost.addHeader(key, headerMap.get(key));
				}
			}

			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

			log.info("1. 요청 URL[{}]", targetUrl);
			log.info("==== 요청 처리 결과 리턴 [{}]: \r\n[{}]\r\n[{}]", httpResponse.getStatusLine().getStatusCode(), targetUrl, param);

			// 데이터 수신
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "utf-8"));
				// 리턴되는 메시지를 읽는다 .

				String strLine ="";
				while ((strLine=in.readLine())!= null) {
					returnMsg+=strLine;
				}
				result = Constants.CODE_RET_OK;
			} else {
				returnMsg = "전송 실패("+httpResponse.getStatusLine().getStatusCode()+")";
			}

		} catch (IOException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} finally {
			try {
				// 자원 해제
				if (in != null) {
					in.close();
				}

				// 연결 종료
				httpClient.close();
			} catch (IOException e) {
				log.error("**** 연결 종료 실패 {}", e);
			}
		}

		if (Constants.CODE_RET_NOK.equals(result))
			return "";

		log.info("2. 요청 처리 결과 메시지 [{}]", returnMsg);

		return returnMsg;
	}

	/**
	 * URL PUT 요청
	 * @param targetUrl
	 * @param param
	 * @param headerMap
	 * @return
	 */
	public static String sendReqPUTJson(String targetUrl, String param, HashMap<String, String> headerMap) {
		String result = Constants.CODE_RET_NOK;
		String returnMsg = "";

		if (Utils.isEmpty(targetUrl)) {
			log.info("==== 요청된 URL이 없습니다. ====");
			return "";
		}

		//http client 생성
		CloseableHttpClient httpClient = HttpClients.createDefault();

		BufferedReader in = null;
		try {

			HttpPut httpPut = new HttpPut(targetUrl);
			StringEntity entity = new StringEntity(param, HTTP.UTF_8);
			httpPut.setEntity(entity);

			// 헤더값 설정
			httpPut.addHeader("Content-Type", "Application/json; charset=UTF-8");
			for (final String key : headerMap.keySet()) {
				httpPut.addHeader(key, headerMap.get(key));
			}

			CloseableHttpResponse httpResponse = httpClient.execute(httpPut);

			log.info("1. 요청 URL[{}]", targetUrl);
			log.info("==== 요청 처리 결과 리턴 [{}]: \r\n[{}]\r\n[{}]", httpResponse.getStatusLine().getStatusCode(), targetUrl, param);

			// 데이터 수신
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "utf-8"));
				// 리턴되는 메시지를 읽는다 .

				String strLine ="";
				while ((strLine=in.readLine())!= null) {
					returnMsg+=strLine;
				}
				result = Constants.CODE_RET_OK;
			} else {
				returnMsg = "전송 실패("+httpResponse.getStatusLine().getStatusCode()+")";
			}

		} catch (IOException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} finally {
			try {
				// 자원 해제
				if (in != null) {
					in.close();
				}

				// 연결 종료
				httpClient.close();
			} catch (IOException e) {
				log.error("**** 연결 종료 실패 {}", e);
			}
		}

		if (Constants.CODE_RET_NOK.equals(result))
			return "";

		log.info("2. 요청 처리 결과 메시지 [{}]", returnMsg);

		return returnMsg;
	}

	/**
	 * URL DELETE 요청
	 * @param targetUrl
	 * @param headerMap
	 * @return
	 */
	public static String sendReqDELETEJson(String targetUrl, HashMap<String, String> headerMap) {
		String result = Constants.CODE_RET_NOK;
		String returnMsg = "";

		if (Utils.isEmpty(targetUrl)) {
			log.info("==== 요청된 URL이 없습니다. ====");
			return "";
		}

		//http client 생성
		CloseableHttpClient httpClient = HttpClients.createDefault();

		BufferedReader in = null;
		try {

			HttpDelete httpDelete = new HttpDelete(targetUrl);

			// 헤더값 설정
			httpDelete.addHeader("Content-Type", "Application/json; charset=UTF-8");
			for (final String key : headerMap.keySet()) {
				httpDelete.addHeader(key, headerMap.get(key));
			}

			CloseableHttpResponse httpResponse = httpClient.execute(httpDelete);

			log.info("1. 요청 URL[{}]", targetUrl);
			log.info("==== 요청 처리 결과 리턴 [{}]: \r\n[{}]", httpResponse.getStatusLine().getStatusCode(), targetUrl);

			// 데이터 수신
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "utf-8"));
				// 리턴되는 메시지를 읽는다 .

				String strLine ="";
				while ((strLine=in.readLine())!= null) {
					returnMsg+=strLine;
				}
				result = Constants.CODE_RET_OK;
			} else {
				returnMsg = "전송 실패("+httpResponse.getStatusLine().getStatusCode()+")";
			}

		} catch (IOException e) {
			log.error("**** 요청 처리 실패 {}", e);
		} finally {
			try {
				// 자원 해제
				if (in != null) {
					in.close();
				}

				// 연결 종료
				httpClient.close();
			} catch (IOException e) {
				log.error("**** 연결 종료 실패 {}", e);
			}
		}

		if (Constants.CODE_RET_NOK.equals(result))
			return "";

		log.info("2. 요청 처리 결과 메시지 [{}]", returnMsg);

		return returnMsg;
	}
}
