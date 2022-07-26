package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.mydata.model.BankBA04;
import com.hanwha.tax.batch.mydata.model.BankTransBT01;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 마이데이터 수입
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="mydata_income")
public class MydataIncome {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	  id;					// 순번
	private String	  custId;				// 고객 번호
	private String	  orgCode;				// 정보제공자 기관코드
	private String	  accountNum;			// 계좌번호
	private String	  seqNo;				// 회차번호
	private String	  transDtime;			// 거래일시
	private String	  transNo;				// 거래번호
	private Integer	  seq;					// 일련번호_쿠콘
	private long	  transAmt;				// 거래금액
	private long	  balanceAmt;			// 거래후잔액
	private String	  transType;			// 거래유형_코드
	private String	  transClass;			// 거래구분
	private String	  currencyCode;			// 통화코드
	private String	  isIncome;				// 수입여부 ( Y/N )
	@Column(name="is_33")
	private Character is33;					// 3.3프로 포함여부 ( Y/N )
	private String	  createDt;				// 등록일시
	private String	  updateDt;				// 변경일시


	/*
     * 거래 유형
     */
    public static enum TransType {
    	신규("01"),
    	출금("02"),
    	입금("03"),
    	정정_입금("04"),
    	정정_출금("05"),
    	출금취소_입금("06"),
    	입금취소_출금("07"),
    	기타_입금("98"),
    	기타_출금("99");
		
    	private String code;
		TransType(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	public MydataIncome convertByBank(String cust_id, BankBA04 bank) {
		this.custId = cust_id;
		this.orgCode = bank.get정보제공자_기관코드();
		this.accountNum = bank.get계좌번호();
		this.seqNo = bank.get회차번호();
		this.transDtime = Utils.formatDate(bank.get거래일시(),"yyyy-MM-dd HH:mm:ss");
		this.transNo = bank.get거래번호();
		this.transAmt = (long) Math.floor(Float.parseFloat(bank.get거래금액()));
		this.balanceAmt = (long) Math.floor(Float.parseFloat(bank.get거래후잔액()));
		this.transType = bank.get거래유형_코드();
		this.transClass = bank.get거래구분();
		this.currencyCode = bank.get통화코드();

		return this;
	}

	public MydataIncome convertByBankTrans(String cust_id, BankTransBT01 bankTrans) {
		this.custId = cust_id;
		this.orgCode = bankTrans.get정보제공자_기관코드();
		this.accountNum = bankTrans.get계좌번호();
		this.seqNo = bankTrans.get회차번호();
		this.transDtime = Utils.formatDate(bankTrans.get거래일시(),"yyyy-MM-dd HH:mm:ss");
		this.transNo = bankTrans.get거래번호();
		this.seq = Utils.isEmpty(bankTrans.get순번_랭크()) ? 0 : Integer.parseInt(bankTrans.get순번_랭크());
		this.transAmt = (long) Math.floor(Float.parseFloat(bankTrans.get거래금액()));
		this.balanceAmt = (long) Math.floor(Float.parseFloat(bankTrans.get거래후잔액()));
		this.transType = bankTrans.get거래유형_코드();
		this.transClass = bankTrans.get거래구분();
		this.currencyCode = bankTrans.get통화코드();
		this.isIncome = bankTrans.get소득구분();
		this.is33 = Utils.isEmpty(bankTrans.get삼점삼프로포함여부()) ? null : bankTrans.get삼점삼프로포함여부().charAt(0);

		return this;
	}
}
