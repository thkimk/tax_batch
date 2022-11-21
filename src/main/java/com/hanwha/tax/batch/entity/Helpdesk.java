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
 * 안내 데스크
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="helpdesk")
public class Helpdesk {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;				// 순번
	private String ansYn;			// 질문 유무
	private String custId;			// 고객 번호
	private String contnet;			// 내용
	private String askDt;			// 질의일시
	private String subject;			// 제목
	private String type;			// 유형
	private String email;			// 이메일
}
