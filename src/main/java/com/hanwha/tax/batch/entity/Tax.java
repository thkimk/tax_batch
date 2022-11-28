package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.tax.model.TaxId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
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
	private String custId;					// 고객 번호
	@Id
	private int    year;					// 연도
	private long   rateTax;					// 경비율 소득세
	private long   bookTax;					// 간편장부 소득세
	private long   income;					// 연도 전체수입
	private long   rateOutgo;				// 경비율 연도 전체지출
	private long   bookOutgo;				// 간편장부 연도 전체지출
	private long   rateMyDeduct;			// 경비율 본인공제
	private long   rateFamilyDeduct;		// 경비율 가족공제
	private long   rateOtherDeduct;			// 경비율 기타공제
	private long   rateIraDeduct;			// 경비율 연금계좌공제
	private long   bookMyDeduct;			// 간편장부 본인공제
	private long   bookFamilyDeduct;		// 간편장부 가족공제
	private long   bookOtherDeduct;			// 간편장부 기타공제
	private long   bookIraDeduct;			// 간편장부 연금계좌공제
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
