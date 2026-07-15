package com.site.transmate.schedule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	List<Schedule> findByAccountId(String accountid);
	List<Schedule> findByDateLike(String date);
}
