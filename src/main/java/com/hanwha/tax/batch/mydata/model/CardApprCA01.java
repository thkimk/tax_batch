package com.hanwha.tax.batch.mydata.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 쿠콘 마이데이터 전문
 * 국내 매핑 승인내역 조회 ( 쿠콘 -> 기관 )
 */
@Slf4j
@Getter
@Setter
@ToString
public class CardApprCA01 extends AbstractMydataCoocon {

	protected String CI;
	protected String 정보제공자_기관코드;
	protected String 카드식별자;
	protected String 승인번호;
	protected String 승인일시;
	protected String 순번_랭크;
	protected String 결제상태_코드;
	protected String 사용구분_코드;
	protected String 정정일시;
	protected String 가맹점명;
	protected String 경비코드;
	protected String 가맹점사업자등록번호;
	protected String 이용금액;
	protected String 정정후금액;
	protected String 조회일시;
	protected String 수정일시;

	/**
	 * data 부 파싱
	 * @param data
	 */
	public void parseData(String data) {
		String[] dataArr = data != null ? data.split("\\|") : null;

		// data 부 검증
		if (dataArr == null || dataArr.length != 17 || !ROW_TYPE.국내매핑승인내역.getCode().equals(dataArr[1])) {
			log.info("국내 매핑 승인내역 조회 파일의 데이터부를 확인해주시기 바랍니다.");
			return;
		}

		// data 부
		CI = dataArr[0];
		식별코드 = dataArr[1];
		정보제공자_기관코드 = dataArr[2];
		카드식별자 = dataArr[3];
		승인번호 = dataArr[4];
		승인일시 = dataArr[5];
		순번_랭크 = dataArr[6];
		결제상태_코드 = dataArr[7];
		사용구분_코드 = dataArr[8];
		정정일시 = dataArr[9];
		가맹점명 = dataArr[10];
		경비코드 = dataArr[11];
		가맹점사업자등록번호 = dataArr[12];
		이용금액 = dataArr[13];
		정정후금액 = dataArr[14];
		조회일시 = dataArr[15];
		수정일시 = dataArr[16];
	}

	/**
	 * data 부 생성
	 * @return
	 */
	public String getData() {
		StringBuffer sb = new StringBuffer();

		sb.append(CI);sb.append("|");
		sb.append(ROW_TYPE.국내매핑승인내역.getCode());sb.append("|");
		sb.append(정보제공자_기관코드);sb.append("|");
		sb.append(카드식별자);sb.append("|");
		sb.append(승인번호);sb.append("|");
		sb.append(승인일시);sb.append("|");
		sb.append(순번_랭크);sb.append("|");
		sb.append(결제상태_코드);sb.append("|");
		sb.append(사용구분_코드);sb.append("|");
		sb.append(정정일시);sb.append("|");
		sb.append(가맹점명);sb.append("|");
		sb.append(경비코드);sb.append("|");
		sb.append(가맹점사업자등록번호);sb.append("|");
		sb.append(이용금액);sb.append("|");
		sb.append(정정후금액);sb.append("|");
		sb.append(조회일시);sb.append("|");
		sb.append(수정일시);sb.append("|");
		sb.append("\n");

		return sb.toString();
	}
}
