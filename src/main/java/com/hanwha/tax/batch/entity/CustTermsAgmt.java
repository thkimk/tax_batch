package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.cust.model.CustTermsAgmtId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * 고객 약관 동의
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="cust_terms_agmt")
@IdClass(CustTermsAgmtId.class)
public class CustTermsAgmt implements Serializable  {
	@Id
	@Column(name="cust_id")
	private String custId;			// 고객 번호
	@Id
	@Column(name="terms_id")
	private long   termsId;			// 약관 번호
	private String isAgree;			// 동의 여부
	private String agmtDt;			// 동의 일시
}
