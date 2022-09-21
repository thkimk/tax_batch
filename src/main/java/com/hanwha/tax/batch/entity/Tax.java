package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.tax.model.TaxId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 예상소득세
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="tax")
@IdClass(TaxId.class)
public class Tax implements Serializable {
	@Id
	@Column(name="cust_id")
	private String custId;				// 고객 번호
	@Id
	private int    year;				// 연도
	private long   rateTax;				// 경비율 소득세
	private long   bookTax;				// 간편장부 소득세
	private String createDt;			// 등록일시
	private String updateDt;			// 변경일시

	/**
	 * insert 되기전 (persist 되기전) 실행된다.
	 * */
	@PrePersist
	public void prePersist() {
		this.createDt = this.createDt == null ? Utils.getCurrentDateTime() : this.createDt;
	}
}
