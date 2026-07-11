package com.site.transmate.transmate.account;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.site.transmate.transmate.meeting.Meeting;
import com.site.transmate.transmate.meeting.MeetingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController	// RestApi용 컨트롤러, 데이터(JSON)반환
public class AccountController {

    private final MeetingRepository meetingRepository;
    private final AccountRepository accountRepository;
	
	//GET
	@GetMapping("/account/{accountid}")
	public Account account(@PathVariable String accountid) {
		Optional<Account> oa = this.accountRepository.findByAccountid(accountid);
		Account a = oa.get();
		Account ret = new Account();
		ret.setId(a.getId());
		ret.setAccountid(a.getAccountid());
		ret.setName(a.getName());
		ret.setPassword(a.getPassword());
		return ret;
	}
	
	//POST
	@PostMapping("/account/create")
	public void create(@RequestBody Map<String,String> requestData) {

	    Account account = new Account();

	    requestData.forEach((key, value) -> {
			if("accountid".equals(key)) {
				account.setAccountid(value);
			}
			if("name".equals(key)) {
				account.setName(value);
			}
			if("password".equals(key)) {
				account.setPassword(value);
			}
	    });
			
		this.accountRepository.save(account);

	}
}

