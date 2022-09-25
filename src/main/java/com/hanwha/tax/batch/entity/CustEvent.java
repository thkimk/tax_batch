package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.cust.model.CustDeductId;
import com.hanwha.tax.batch.cust.model.CustEventId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * 고객 이벤트
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="cust_event")
@IdClass(CustEventId.class)
public class CustEvent implements Serializable {
	@Id
	@Column(name="cust_id")
	private String    custId;			// 고객번호
	@Id
	@Column(name="event_id")
	private String    eventId;			// 이벤트번호
	private Character result;			// 이벤트결과
	private String    joinDt;			// 응모일시
}
