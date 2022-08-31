package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 단말기 정보
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="dev_info")
public class DevInfo {
	@Id
	private String custId;			// 고객 번호
	private String devUid;			// 단말기 UID
	private String devName;			// 단말기명
	private String osType;			// OS 타입
	private String osVer;			// OS 버전
	private String pushToken;		// 푸시 토큰
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시
}
