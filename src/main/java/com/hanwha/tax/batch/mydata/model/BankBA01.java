package com.hanwha.tax.batch.mydata.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 쿠콘 마이데이터 전문
 * 은행 계좌 목록 조회 ( 쿠콘 -> 기관 )
 */
@Slf4j
@Getter
@Setter
@ToString
public class BankBA01 extends AbstractMydataCoocon {

	private String CI;
	private String 정보제공자_기관코드;
	private String 계좌번호;
	private String 상품명;
	private String 회차번호;
	private String 외화계좌여부;
	private String 마이너스약정여부;
	private String 계좌구분;
	private String 계좌상태;
	private String 조회일시;

	/**
	 * data 부 파싱
	 * @param data
	 */
	public void parseData(String data) {
		String[] dataArr = data != null ? data.split("\\|") : null;

		// data 부 검증
		if (dataArr == null || dataArr.length != 11 || !ROW_TYPE.은행계좌목록.getCode().equals(dataArr[1])) {
			log.info("은행 계좌 목록 파일의 데이터부를 확인해주시기 바랍니다.");
			return;
		}

		// data 부
		CI = dataArr[0];
		식별코드 = dataArr[1];
		정보제공자_기관코드 = dataArr[2];
		계좌번호 = dataArr[3];
		상품명 = dataArr[4];
		회차번호 = dataArr[5];
		외화계좌여부 = dataArr[6];
		마이너스약정여부 = dataArr[7];
		계좌구분 = dataArr[8];
		계좌상태 = dataArr[9];
		조회일시 = dataArr[10];
	}

	/**
	 * data 부 생성
	 * @return
	 */
	public String getData() {
		StringBuffer sb = new StringBuffer();

		sb.append(CI);sb.append("|");
		sb.append(ROW_TYPE.은행계좌목록.getCode());sb.append("|");
		sb.append(정보제공자_기관코드);sb.append("|");
		sb.append(계좌번호);sb.append("|");
		sb.append(상품명);sb.append("|");
		sb.append(회차번호);sb.append("|");
		sb.append(외화계좌여부);sb.append("|");
		sb.append(마이너스약정여부);sb.append("|");
		sb.append(계좌구분);sb.append("|");
		sb.append(계좌상태);sb.append("|");
		sb.append(조회일시);sb.append("|");
		sb.append("\n");

		return sb.toString();
	}
}
