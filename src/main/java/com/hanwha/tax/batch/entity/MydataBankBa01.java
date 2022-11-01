package com.hanwha.tax.batch.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 은행(원본) : 은행 계좌 목록
 */
@Getter
@Setter
@ToString
@DynamicInsert
@NoArgsConstructor
@Entity(name="mydata_bank_ba01")
public class MydataBankBa01 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long   id;			// 순번
	private String rowInfo;		// ROW 데이터

	public MydataBankBa01(String rowInfo) {
		this.rowInfo = rowInfo;
	}
}