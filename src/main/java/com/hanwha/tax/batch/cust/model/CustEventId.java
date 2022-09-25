package com.hanwha.tax.batch.cust.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 고객 이벤트 PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustEventId implements Serializable {
	private String custId;			// 고객 번호
	private String eventId;			// 이벤트 번호
}
