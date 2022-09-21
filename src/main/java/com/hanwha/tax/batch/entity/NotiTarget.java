package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.mydata.model.BankTrans;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * 알람 대상
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="noti_target")
public class NotiTarget {
	@Id
	private String custId;			// 고객 번호
	private String name;			// 이름
	private String email;			// 이메일
	private String mobile;			// 휴대폰
	private String custGrade;		// 회원등급
	private String gender;			// 성별
	private String age;				// 나이
	private String marriage;		// 결혼
	private String family;			// 부양가족
	private String income;			// 소득
	private String newBusin;		// 사업자구분

	/*
	 * 회원 등급
	 */
	public static enum CustGrade {
		전체("00"),
		정회원("01"),
		준회원("02");

		private final String code;
		CustGrade(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 성별
	 */
	public static enum Gender {
		전체("00"),
		남자("01"),
		여자("02");

		private final String code;
		Gender(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 나이
	 */
	public static enum Age {
		전체("00"),
		십대("01"),
		이십대("02"),
		삼십대("03"),
		사십대("04"),
		오십대("05"),
		육십대("06"),
		칠십대이상("07");

		private final String code;
		Age(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 혼인여부
	 */
	public static enum Marriage {
		전체("00"),
		기혼("01"),
		미혼("02");

		private final String code;
		Marriage(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 부양가족
	 */
	public static enum Family {
		전체("00"),
		한명("01"),
		두명("02"),
		세명("03"),
		네명("04"),
		다섯명이상("05");

		private final String code;
		Family(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 소득
	 */
	public static enum Income {
		전체("00"),
		일천만원("01"),
		이천만원("02"),
		삼천만원("03"),
		사천만원("04"),
		오천만원("05"),
		육천만원("06"),
		칠천만원("07"),
		칠천오백만원이상("08");

		private final String code;
		Income(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	/*
	 * 사업자구분
	 */
	public static enum NewBusin {
		전체("00"),
		신규사업자("01"),
		계속사업자("02");

		private final String code;
		NewBusin(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}

	public NotiTarget convertByCust(Cust cust, CustInfo custInfo, CustInfoDtl custInfoDtl, CustDeduct custDeduct, List<CustFamily> custFamilyList) {
		if (cust != null) {
			this.custId = cust.getCustId();
			this.custGrade = Cust.CustGrade.정회원.getCode().equals(cust.getCustGrade()) ? CustGrade.정회원.getCode() :
							 Cust.CustGrade.준회원.getCode().equals(cust.getCustGrade()) ? CustGrade.준회원.getCode() : CustGrade.전체.getCode();
		}

		if (custInfo != null) {
			int realAge = Utils.realAge(custInfo.getBirth());	// 고객 만나이

			this.name = custInfo.getName();
			this.email = custInfo.getEmail();
			this.mobile = custInfo.getMobile();
			this.gender = CustInfo.Gender.남자.getCode().equals(custInfo.getGender()) ? Gender.남자.getCode() :
						  CustInfo.Gender.여자.getCode().equals(custInfo.getGender()) ? Gender.여자.getCode() : Gender.전체.getCode();
			this.age = realAge < 10 ? Age.전체.getCode() :
					   realAge < 20 ? Age.십대.getCode() :
					   realAge < 30 ? Age.이십대.getCode() :
					   realAge < 40 ? Age.삼십대.getCode() :
					   realAge < 50 ? Age.사십대.getCode() :
					   realAge < 60 ? Age.오십대.getCode() :
					   realAge < 70 ? Age.육십대.getCode() : Age.칠십대이상.getCode();

		}

		if (custInfoDtl != null) {
			this.marriage = "Y".equals(custInfoDtl.getIsMarriage()) ? Marriage.기혼.getCode() :
							"N".equals(custInfoDtl.getIsMarriage()) ? Marriage.미혼.getCode() : Marriage.전체.getCode();
			this.newBusin = "Y".equals(custInfoDtl.getIsNewBusin()) ? NewBusin.신규사업자.getCode() :
							"N".equals(custInfoDtl.getIsNewBusin()) ? NewBusin.계속사업자.getCode() : NewBusin.전체.getCode();
		}

		if (custDeduct != null) {
			this.income = 10000000 <= custDeduct.getIncome() ? Income.일천만원.getCode() :
						  20000000 <= custDeduct.getIncome() ? Income.이천만원.getCode() :
						  30000000 <= custDeduct.getIncome() ? Income.삼천만원.getCode() :
						  40000000 <= custDeduct.getIncome() ? Income.사천만원.getCode() :
						  50000000 <= custDeduct.getIncome() ? Income.오천만원.getCode() :
						  60000000 <= custDeduct.getIncome() ? Income.육천만원.getCode() :
						  70000000 <= custDeduct.getIncome() ? Income.칠천만원.getCode() :
						  75000000 <= custDeduct.getIncome() ? Income.칠천오백만원이상.getCode() : Income.전체.getCode();
		}

		if (custFamilyList != null) {
			int cntFamily = custFamilyList.size();	// 부양 가족수
			this.family = 1 == cntFamily ? Family.한명.getCode() :
						  2 == cntFamily ? Family.두명.getCode() :
						  3 == cntFamily ? Family.세명.getCode() :
						  4 == cntFamily ? Family.네명.getCode() :
						  5 <= cntFamily ? Family.다섯명이상.getCode() : Family.전체.getCode();
		}

		return this;
	}
}
