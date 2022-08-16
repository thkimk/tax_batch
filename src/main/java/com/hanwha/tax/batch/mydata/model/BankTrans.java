package com.hanwha.tax.batch.mydata.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 쿠콘 마이데이터 전문
 * 은행 수신 계좌 매핑 거래내역조회 ( 쿠콘 -> 기관 )
 */
@Slf4j
@Getter
@Setter
@ToString
public class BankTrans extends AbstractMydataCoocon {

	protected String CI;
	protected String 정보제공자_기관코드;
	protected String 계좌번호;
	protected String 회차번호;
	protected String 거래일시;
	protected String 거래번호;
	protected String 순번_랭크;
	protected String 거래유형_코드;
	protected String 거래구분;
	protected String 통화코드;
	protected String 거래금액;
	protected String 거래후잔액;
	protected String 소득구분;
	protected String 삼점삼프로포함여부;
	protected String 조회일시;
	protected String 수정일시;

	public void parseData(String data) {
		String[] dataArr = data != null ? data.split("|") : null;

		// data 부 검증
		if (dataArr == null || dataArr.length != 18 || !AbstractMydataCoocon.ROW_TYPE.은행수신계좌매핑거래내역조회.getCode().equals(dataArr[1])) {
			log.info("은행 수신 계좌 매핑 거래내역조회 파일의 데이터부를 확인해주시기 바랍니다.");
			return;
		}

		// data 부
		CI = dataArr[0];
		식별코드 = dataArr[1];
		정보제공자_기관코드 = dataArr[2];
		계좌번호 = dataArr[3];
		회차번호 = dataArr[4];
		거래일시 = dataArr[5];
		거래번호 = dataArr[6];
		순번_랭크 = dataArr[7];
		거래유형_코드 = dataArr[8];
		거래구분 = dataArr[9];
		통화코드 = dataArr[10];
		거래금액 = dataArr[11];
		거래후잔액 = dataArr[12];
		소득구분 = dataArr[13];
		삼점삼프로포함여부 = dataArr[14];
		조회일시 = dataArr[15];
		수정일시 = dataArr[16];
	}
}
