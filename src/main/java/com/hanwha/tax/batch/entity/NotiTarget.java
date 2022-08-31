package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;

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
	private String newBusin;		// 사업자
	private String creater;			// 등록자
	private String updater;			// 변경자
}
