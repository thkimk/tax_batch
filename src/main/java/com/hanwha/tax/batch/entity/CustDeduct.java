package com.hanwha.tax.batch.entity;

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
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시
}
