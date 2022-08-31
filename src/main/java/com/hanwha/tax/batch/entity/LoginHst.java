package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 로그인 이력
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="login_hst")
public class LoginHst {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;				// 순번
	private String custId;			// 고객 번호
	private String loginDt;			// 로그인일시
	private String authType;		// 인증 분류
	private String devUid;			// 단말기 UID
	private String devName;			// 단말기 명
	private String osName;			// OS 명
	private String authStatus;		// 인증 상태
}
