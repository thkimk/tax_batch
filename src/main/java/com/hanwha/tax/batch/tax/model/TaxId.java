package com.hanwha.tax.batch.tax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 예상소득세 PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxId implements Serializable {
	private String custId;			// 고객 번호
	private int    year;			// 연도
}
