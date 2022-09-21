package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 고객 정보 상세
 */
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@Entity(name="cust_info_dtl")
public class CustInfoDtl {
	@Id
	private String 	  custId;			// 고객 번호
	private String 	  indstCode;		// 업종 코드
	private Character isDisorder;		// 장애 여부
	private Character isHshld;			// 세대주 여부
	private Character isNewBusin;		// 신규사업자 여부
	private Character isMarriage;		// 결혼 여부
	private Character isSinParent;		// 한부모 여부
	private String 	  taxFlag;			// 소득세 계산 flag
	private String 	  mydataDt;			// 자산변경일시
	private String 	  createDt;			// 등록일시
	private String 	  updateDt;			// 변경일시
}
