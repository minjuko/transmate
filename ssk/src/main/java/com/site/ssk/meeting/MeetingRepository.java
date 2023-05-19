package com.site.ssk.meeting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.site.ssk.account.Account;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
	List<Meeting> findByAccountId(String accountid);
	List<Meeting> findByTitleLike(String title);
	
}