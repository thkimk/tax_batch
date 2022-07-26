package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.mydata.model.CardApprCA01;
import com.hanwha.tax.batch.mydata.model.CardCD03;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 마이데이터 경비
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="mydata_outgoing")
public class MydataOutgoing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long    id;					// 순번
	private String  custId;				// 고객 번호
	private String  orgCode;			// 정보제공자 기관코드
	private String  cardId;				// 카드식별자
	private Integer seq;				// 일련번호_쿠콘
	private String  payType;			// 사용구분 ( 신용/체크 )
	private String  status;				// 결제상태_코드
	private String  transDtime;			// 정정일시
	private String  apprNum;			// 승인번호
	private long    apprAmt;			// 이용금액
	private long    modAmt;				// 정정후금액
	private String  apprDtime;			// 승인일시
	private String  merchantName;		// 가맹점명
	private String  category;			// 경비코드
	private String  createDt;			// 등록일시
	private String  updateDt;			// 변경일시
	

	/*
     * 거래 유형
     */
    public static enum PayType {
    	신용("01"),
    	체크("02");
		
    	private String code;
		PayType(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 결재 상태
	 */
	public static enum ApprStatus {
		승인("01"),
		승인취소("02"),
		정정("03");

		private String code;
		ApprStatus(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 카드 경비
	 */
	public static enum CardCategory {
		제세공과금("01"),
		지급이자("02"),
		접대비("03"),
		차량유지비("04"),
		지급수수료("05"),
		소모품비("06"),
		광고선전비("07"),
		여비교통비("08"),
		기타("09"),
		승인취소("10"),
		경비제외("99");

		private String code;
		CardCategory(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	public MydataOutgoing convertByCard(String custId, CardCD03 card) {
		this.custId = custId;
		this.orgCode = card.get정보제공자_기관코드();
		this.cardId = card.get카드식별자();
		this.payType = card.get사용구분_코드();
		this.status = card.get결제상태_코드();
		this.transDtime = Utils.formatDate(card.get정정일시(),"yyyy-MM-dd HH:mm:ss");
		this.apprNum = card.get승인번호();
		this.apprAmt = (long) Math.floor(Float.parseFloat(card.get이용금액()));
		this.modAmt = Long.parseLong(card.get정정후금액());
		this.apprDtime = Utils.formatDate(card.get승인일시(),"yyyy-MM-dd HH:mm:ss");
		this.merchantName = card.get가맹점명();

		return this;
	}

	public MydataOutgoing convertByCardAppr(String custId, CardApprCA01 cardAppr) {
		this.custId = custId;
		this.orgCode = cardAppr.get정보제공자_기관코드();
		this.cardId = cardAppr.get카드식별자();
		this.seq = Utils.isEmpty(cardAppr.get순번_랭크()) ? 0 : Integer.parseInt(cardAppr.get순번_랭크());
		this.payType = cardAppr.get사용구분_코드();
		this.status = cardAppr.get결제상태_코드();
		this.transDtime = Utils.formatDate(cardAppr.get정정일시(),"yyyy-MM-dd HH:mm:ss");
		this.apprNum = cardAppr.get승인번호();
		this.apprAmt = (long) Math.floor(Float.parseFloat(cardAppr.get이용금액()));
		this.modAmt = Long.parseLong(cardAppr.get정정후금액());
		this.apprDtime = Utils.formatDate(cardAppr.get승인일시(),"yyyy-MM-dd HH:mm:ss");
		this.merchantName = cardAppr.get가맹점명();
		this.category = Utils.lpadByte(cardAppr.get경비코드(),2,"0");

		return this;
	}
}
