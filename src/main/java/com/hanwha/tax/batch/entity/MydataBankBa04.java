package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

/**
 * 은행(원본) : 은행 수신 계좌 거래내역
 */
@Getter
@Setter
@ToString
@DynamicInsert
@NoArgsConstructor
@Entity(name="mydata_bank_ba04")
public class MydataBankBa04 {
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

	public MydataBankBa04(String rowInfo) {
		this.rowInfo = rowInfo;
	}
}
