package com.hanwha.tax.batch.event.repository;

import com.hanwha.tax.batch.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    @Query(value="select e.* from event e where :ymdBasic between e.begin_dt and e.end_dt", nativeQuery = true)
    List<Event> getEventListOngoing(String ymdBasic);
}