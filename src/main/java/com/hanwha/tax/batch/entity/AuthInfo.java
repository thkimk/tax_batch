package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 인증
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="auth_info")
public class AuthInfo {
	@Id
	private String custId;			// 고객 번호
	private String authType;		//
	private String pin;				// 핀 번호
	private String ci;				// 본인확인 인증키
	private String isMain;			// 대표여부
	private String authStatus;		// 최종 상태코드
	private String uid;				// 디바이스 UUID
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시


	/*
     * 인증 상태
     */
    public static enum AuthStatus {
    	활성("00"),
    	잠김("01"),
    	만료("02");
		
    	private String code;
		AuthStatus(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}
}
