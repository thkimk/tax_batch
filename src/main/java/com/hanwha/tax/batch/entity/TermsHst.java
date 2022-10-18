package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 이용약관 동의 이력
 */
@Getter
@Setter
@ToString
@Entity(name="terms_hst")
public class TermsHst {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;				// 순번
	private String custId;			// 고객 번호
	private long   termsId;			// 약관 아이디
	private String termsName;		// 약관 이름
	private char   isAgree;			// 동의 여부
	private String agreeDt;			// 동읠 일시
}
