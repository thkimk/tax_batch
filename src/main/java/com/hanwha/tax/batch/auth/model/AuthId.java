package com.hanwha.tax.batch.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 인증 PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthId implements Serializable {
	private String custId;			// 고객 번호
	private String pin;				// 핀 번호
}
