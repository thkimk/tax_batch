package com.hanwha.tax.batch.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 이벤트 현황 PK
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusId implements Serializable {
	private String eventId;			// 이벤트 아이디
	private String day;				// 날짜
}
