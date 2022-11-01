package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

/**
 * 카드(원본) : 대출상품 목록
 */
@Getter
@Setter
@ToString
@DynamicInsert
@NoArgsConstructor
@Entity(name="mydata_card_cd31")
public class MydataCardCd31 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;			// 순번
	private String rowInfo;		// ROW 데이터
	private String createDt;	// 등록일시

	/**
	 * insert 되기전 (persist 되기전) 실행된다.
	 * */
	@PrePersist
	public void prePersist() {
		this.createDt = this.createDt == null ? Utils.getCurrentDateTime() : this.createDt;
	}

	public MydataCardCd31(String rowInfo) {
		this.rowInfo = rowInfo;
	}
}
