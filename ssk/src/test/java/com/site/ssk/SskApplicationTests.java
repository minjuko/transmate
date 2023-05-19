package com.site.ssk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.site.ssk.account.Account;
import com.site.ssk.account.AccountRepository;
import com.site.ssk.meeting.Meeting;
import com.site.ssk.meeting.MeetingRepository;

@SpringBootTest
class SskApplicationTests {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
    private MeetingRepository meetingRepository;
	
	@Transactional
	@Test
	void testJpa() {
		/*
		Optional<Account> oq = this.accountRepository.findById(1);
		Account q = oq.get();
		
		Meeting m1 = new Meeting();
		m1.setData("11111");
		m1.setSummary_data("22222");
		m1.setTitle("33333");
		m1.setCreateDate(LocalDateTime.now());
		m1.setAccount(q);
		this.meetingRepository.save(m1);
		*/
        
	}

}
