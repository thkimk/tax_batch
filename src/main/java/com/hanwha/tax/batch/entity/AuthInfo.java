package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.auth.model.AuthId;
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
 * 인증
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="auth_info")
@IdClass(AuthId.class)
public class AuthInfo implements Serializable {
	@Id
	@Column(name="cust_id")
	private String custId;			// 고객 번호
	@Id
	private String pin;				// 핀 번호
	private String ci;				// 본인확인 인증키
	private String isMain;			// 대표여부
	private String authStatus;		// 최종 상태코드
	private int    failCnt;			// 인증실패횟수
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시


	/*
     * 인증 상태
     */
    public static enum AuthStatus {
    	활성("00"),
    	잠김("01"),
    	만료("02");
		
    	private final String code;
		AuthStatus(String code) { this.code = code; }
		public String getCode() { return this.code; }
	}
}
