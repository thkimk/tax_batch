package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 전체 경비
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="total_outgoing")
public class TotalOutgoing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;					// 순번
	private String custId;				// 고객 번호
	private int    year;				// 연도
	private int    month;				// 월
	private Long   amount;				// 수입금액
	private String createDt;			// 등록일시
	private String updateDt;			// 변경일시

	public TotalOutgoing(String custId, int year, int month, long amount) {
		this.custId = custId;
		this.year = year;
		this.month = month;
		this.amount = amount;
	}
}
