package com.hanwha.tax.batch.mydata.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 쿠콘 마이데이터 전문
 * 제3자 제공동의 철회 ( 기관 -> 쿠콘 )
 */
@Slf4j
@Getter
@Setter
@ToString
public class RevokeCI extends AbstractMydataCoocon {

	private String CI;
	private String 요청일시;

	/**
	 * data 부 파싱
	 * @param data
	 */
	public void parseData(String data) {
		String[] dataArr = data != null ? data.split("\\|") : null;

		// data 부 검증
		if (dataArr == null || dataArr.length != 3 || !ROW_TYPE.데이터레코드부.getCode().equals(dataArr[0])) {
			log.info("은행 수신 계좌 매핑 거래내역조회 파일의 데이터부를 확인해주시기 바랍니다.");
			return;
		}

		// data 부
		식별코드 = dataArr[0];
		CI = dataArr[1];
		요청일시 = dataArr[2];
	}

	/**
	 * data 부 생성
	 * @return
	 */
	public String getData() {
		StringBuffer sb = new StringBuffer();

		sb.append(ROW_TYPE.데이터레코드부.getCode());sb.append("|");
		sb.append(CI);sb.append("|");
		sb.append(요청일시);sb.append("|");
		sb.append("\n");

		return sb.toString();
	}
}
