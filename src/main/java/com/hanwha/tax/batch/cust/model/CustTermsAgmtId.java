package com.hanwha.tax.batch.cust.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 고객 약관 동의 PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustTermsAgmtId implements Serializable {
	private String custId;			// 고객 번호
	private long   termsId;			// 약관 번호
}
