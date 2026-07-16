package com.site.transmate.account;

import java.util.Map;
import com.site.transmate.meeting.MeetingRepository;
import com.site.transmate.api.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
		Account a = this.accountRepository.findByAccountid(accountid)
				.orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
		Account ret = new Account();
		ret.setId(a.getId());
		ret.setAccountid(a.getAccountid());
		ret.setName(a.getName());
		ret.setPassword(a.getPassword());
		return ret;
	}
	
	//POST
	@PostMapping("/account/create")
	public ResponseEntity<Void> create(@RequestBody Map<String,String> requestData) {

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
		return ResponseEntity.status(HttpStatus.CREATED).build();

	}
}

