package com.hanwha.tax.batch.cust.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 고객  PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustDeductId implements Serializable {
	private String custId;			// 고객 번호
	private int    year;			// 연도
}
