package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 고객 등급 현황
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="cust_grade_status")
public class CustGradeStatus {
	@Id
	private String 	  basicYmd;			// 기준일
	private int 	  newCnt;			// 신규회원 수
	private int 	  regCnt;			// 정회원 수
	private int 	  asctCnt;			// 준회원 수
	private int 	  outCnt;			// 탈퇴회원 수
	private int 	  regAccCnt;		// 누적 정회원 수
	private int 	  asctAccCnt;		// 누적 준회원 수
	private int 	  outAccCnt;		// 누적 탈퇴회원 수
}
