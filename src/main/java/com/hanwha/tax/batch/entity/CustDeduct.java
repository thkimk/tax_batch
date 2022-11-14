package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.model.CustDeductId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * 고객 자산
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="cust_deduct")
@IdClass(CustDeductId.class)
public class CustDeduct implements Serializable {
	@Id
	@Column(name="cust_id")
	private String custId;			// 고객 번호
	@Id
	private int    year;			// 연도
	private long   income;			// 수입
	private long   outgoing;		// 지출
	private Long   medAmt;			// 소상공인 공제부금 보유 납입액
	private Long   npcAmt;			// 국민연금보험료 납입액
	private Long   rspAmt;			// 연금저축 보유 납입액
	private Long   sedAmt;			// 중소기업창업투자 금액
	private Long   iraAmt;			// 연금저출계좌보유 납입액
	private Long   irpAmt;			// 퇴직연금 납입액
	@Column(name="sed01_amt")
	private Long   sed01Amt;		// 벤처투자조합 및 투자기구
	@Column(name="sed02_amt")
	private Long   sed02Amt;		// 벤처기업투자신탁 수익증권
	@Column(name="sed03_amt")
	private Long   sed03Amt;		// 개인투자조합
	@Column(name="sed04_amt")
	private Long   sed04Amt;		// 벤처기업 및 크라우드 펀딩
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시

	public void updateNull() {
		npcAmt = Utils.nullToZero(npcAmt);
		rspAmt = Utils.nullToZero(rspAmt);
		medAmt = Utils.nullToZero(medAmt);
		sedAmt = Utils.nullToZero(sedAmt);
		iraAmt = Utils.nullToZero(iraAmt);
		irpAmt = Utils.nullToZero(irpAmt);

		sed01Amt = Utils.nullToZero(sed01Amt);
		sed02Amt = Utils.nullToZero(sed02Amt);
		sed03Amt = Utils.nullToZero(sed03Amt);
		sed04Amt = Utils.nullToZero(sed04Amt);
	}
}
