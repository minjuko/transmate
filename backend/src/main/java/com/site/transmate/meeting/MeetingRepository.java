package com.site.transmate.meeting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
	List<Meeting> findByTitleLike(String title);
	
}
