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
 * 고객 가족
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="cust_family")
public class CustFamily {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   familySeq;		// 순번
	private String custId;			// 고객 번호
	private String family;			// 가족관계
	private String birth;			// 생년월일
	private String isDisOrder;		// 준회원 가입일시
	private int    order1;			// 자녀 순서
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시
}
