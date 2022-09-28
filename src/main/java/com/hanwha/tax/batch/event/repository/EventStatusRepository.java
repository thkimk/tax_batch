package com.hanwha.tax.batch.event.repository;

import com.hanwha.tax.batch.entity.EventStatus;
import com.hanwha.tax.batch.event.model.EventStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, EventStatusId> {
}