package com.hanwha.tax.batch.entity;

import com.hanwha.tax.batch.event.model.EventStatusId;
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
 * 이벤트 현황
 */
@Getter
@Setter
@ToString
@DynamicInsert
@Entity(name="event_status")
@IdClass(EventStatusId.class)
public class EventStatus implements Serializable {
	@Id
	@Column(name="event_id")
	private String	eventId;		// 이벤트 아이디
	@Id
	private String	day;			// 날짜
	private Integer clickCnt;		// 클릭수
	private Integer joinCnt;		// 참여자수
}
