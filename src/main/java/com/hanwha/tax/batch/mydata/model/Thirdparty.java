package com.hanwha.tax.batch.mydata.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 쿠콘 마이데이터 전문
 * 제3자 제공동의 회원 ( 쿠콘 -> 기관 )
 */
@Slf4j
@Getter
@Setter
@ToString
public class Thirdparty extends AbstractMydataCoocon {

	protected String CI;
	protected String 쿠콘제3자제공동의1;
	protected String 최종변경일시;
	protected String 변경구분;

	/**
	 * data 부 파싱
	 * @param data
	 */
	public void parseData(String data) {
		String[] dataArr = data != null ? data.split("\\|") : null;

		// data 부 검증
		if (dataArr == null || dataArr.length != 5 || !ROW_TYPE.데이터레코드부.getCode().equals(dataArr[0])) {
			log.info("제3자 제공동의 회원 파일의 데이터부를 확인해주시기 바랍니다.");
			return;
		}

		// data 부
		식별코드 = dataArr[0];
		CI = dataArr[1];
		쿠콘제3자제공동의1 = dataArr[2];
		최종변경일시 = dataArr[3];
		변경구분 = dataArr[4];
	}

	/**
	 * data 부 생성
	 * @return
	 */
	public String getData() {
		StringBuffer sb = new StringBuffer();

		sb.append(ROW_TYPE.데이터레코드부.getCode());sb.append("|");
		sb.append(CI);sb.append("|");
		sb.append(쿠콘제3자제공동의1);sb.append("|");
		sb.append(최종변경일시);sb.append("|");
		sb.append(변경구분);sb.append("|");
		sb.append("\n");

		return sb.toString();
	}
}
