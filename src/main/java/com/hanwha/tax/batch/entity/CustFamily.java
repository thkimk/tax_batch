package com.hanwha.tax.batch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

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
	private long      familySeq;		// 순번
	private String    custId;			// 고객 번호
	private String    family;			// 가족관계
	private String    birth;			// 생년월일
	private Character isDisorder;		// 준회원 가입일시
	private Integer   order1;			// 자녀 순서
	private String    createDt;			// 등록일시
	private String    updateDt;			// 변경일시

	/*
	 * 가족 유형
	 */
	public static enum TypeFamily {
		없음("-1"),
		조부모("00"),
		외조부모("01"),
		부모님("02"),
		배우자부모님("03"),
		배우자("04"),
		형제_자매("05"),
		자녀("06"),
		손자_손녀("07"),
		위탁자녀("08"),
		RECEIVER("09");

		private String code;
		TypeFamily(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	public static boolean existPartner(List<CustFamily> custFamilyList) {
		for (final CustFamily custFamily : custFamilyList) {
			if (TypeFamily.배우자.getCode().equals(custFamily.getFamily())) return true;
		}
		return false;
	}
}
