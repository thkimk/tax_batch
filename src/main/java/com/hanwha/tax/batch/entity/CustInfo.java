package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 고객 정보
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="cust_info")
public class CustInfo {
	@Id
	private String custId;			// 고객 번호
	private String name;			// 이름
	private String email;			// 이메일
	private String birth;			// 생년월일
	private String mobile;			// 휴대폰번호
	private String telecom;			// 통신사
	private String gender;			// 성별
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시
}
