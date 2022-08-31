package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 안내 데스크 응답
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="helpdesk_ans")
public class HelpdeskAns {
	@Id
	private long   helpdeskId;		// 안내데스크 순번
	private String contnet;			// 내용
	private String ansDt;			// 응답일시
	private String empName;			// 담당자명
}
