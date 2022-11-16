package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;

/**
 * 전체 수입
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="book_income")
public class BookIncome {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	  id;				// 순번
	private String	  custId;			// 고객 번호
	private String	  transDtime;		// 거래일시
	private Long	  transAmt;			// 거래금액
	@Column(name="is_33")
	private Character is33;				// 3.3% 포함여부
	private String	  createDt;			// 등록일시
	private String	  updateDt;			// 변경일시
}
