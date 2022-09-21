package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 업종
 */
@Getter
@Setter
@ToString
@Entity(name="industry")
public class Industry {
	@Id
	private String code;				// 업종코드
	private String name;				// 업종명
	private Float  simpleExrt;			// 단순경비율(일반)
	private Float  simpleExrtExc;		// 단순경비율(초과)
	private Float  standardExrt;		// 기준경비율
	private String searchTerms;			// 연관 검색어
	private String createDt;			// 등록일시
	private String updateDt;			// 변경일시
	private String creater;				// 등록자
	private String updater;				// 변경자
}
