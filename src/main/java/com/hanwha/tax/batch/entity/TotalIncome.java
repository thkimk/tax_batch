package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.PrePersist;
import java.util.Map;

/**
 * 전체 수입
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="total_income")
public class TotalIncome {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	  id;					// 순번
	private long	  fk;					// 수입정보 외래키
	private Character flagFk;				// 외래키 식별값 ( 'M' : 마이데이터, 'B' : 간편장부 )
	private String	  custId;				// 고객 번호
	private int		  year;					// 연도
	private int		  month;				// 월
	private long	  amount;				// 수입금액
	@Column(name="is_33")
	private Character is33;					// 3.3프로 포함여부 ( Y/N )
	private String	  createDt;				// 등록일시
	private String	  updateDt;				// 변경일시

	/**
	 * insert 되기전 (persist 되기전) 실행된다.
	 * */
	@PrePersist
	public void prePersist() {
		this.createDt = this.createDt == null ? Utils.getCurrentDateTime() : this.createDt;
	}

	public TotalIncome convertByMydataMap(Map<String, String> mydataMap) {
		this.fk = Long.parseLong(String.valueOf(mydataMap.get("fk")));
		this.flagFk = Utils.isEmpty(mydataMap.get("flag_fk")) ? null : mydataMap.get("flag_fk").charAt(0);
		this.custId = mydataMap.get("cust_id");
		this.year = Integer.parseInt(String.valueOf(mydataMap.get("year")));
		this.month = Integer.parseInt(String.valueOf(mydataMap.get("month")));
		this.amount = Long.parseLong(String.valueOf(mydataMap.get("amount")));
		this.is33 = Utils.isEmpty(mydataMap.get("is_33")) ? null : mydataMap.get("is_33").charAt(0);

		return this;
	}
}
