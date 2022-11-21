package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 고객
 */
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@Entity(name="cust")
public class Cust {
	@Id
	private String custId;			// 고객 번호
	private String custStatus;		// 고객 상태
	private String custGrade;		// 고객 등급
	private String asctInDt;		// 준회원 가입일시
	private String asctOutDt;		// 준회원 탈퇴일시
	private String regInDt;			// 정회원 가입일시
	private String regOutDt;		// 정회원 탈퇴일시
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시

	/*
	 * 고객 상태
	 */
	public static enum CustStatus {
		정상("00"),
		탈회("02"),
		휴면("03");

		private String code;
		CustStatus(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 고객 등급
	 */
	public static enum CustGrade {
		비회원("00"),
		준회원("01"),
		정회원("02");

		private String code;
		CustGrade(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}
}
