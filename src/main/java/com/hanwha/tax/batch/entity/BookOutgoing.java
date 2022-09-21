package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

/**
 * 전체 경비
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="book_outgoing")
public class BookOutgoing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;				// 순번
	private String custId;			// 고객 번호
	private Long   apprAmt;			// 승인금액
	private String apprDtime;		// 승인일시
	private String merchantName;	// 가맹점명
	private String category;		// 경비코드
	private String createDt;		// 등록일시
	private String updateDt;		// 변경일시
}
