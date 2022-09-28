package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 이벤트
 */
@Getter
@Setter
@ToString
@Entity(name="event")
public class Event {
	@Id
	private String	  id;				// 이벤트코드
	private Character viewYn;			// 노출여부
	private Character topViewYn;		// 상단노출여부
	private String	  subject;			// 제목
	private String	  subCopy;			// 서브카피
	private String	  beginDt;			// 노출 시작일
	private String	  endDt;			// 노출 종료일
	private Long	  imageId;			// 이미지 식별값
	private String	  link;				// 컨텐츠 주소
	private Character winnerYn;			// 당첨자 진행여부
	private String	  createDt;			// 등록일
	private String	  creater;			// 등록자
	private String	  updateDt;			// 변경일
	private String	  updater;			// 변경자
}
