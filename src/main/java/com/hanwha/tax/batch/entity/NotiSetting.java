package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 알람 설정
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="noti_setting")
public class NotiSetting {
	@Id
	private String custId;			// 고객 번호
	private String emailYn;			// 이메일 유무
	private String pushYn;			// 푸시 유무
	private String smsYn;			// 문자 유무
	private String talkYn;			// 알림톡 유무
	private String updateDt;		// 변경일시
}
