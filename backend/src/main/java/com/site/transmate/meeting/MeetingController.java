package com.site.transmate.meeting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController	// RestApi용 컨트롤러, 데이터(JSON)반환
public class MeetingController {

    private final MeetingRepository meetingRepository;
    private final AccountRepository accountRepository;
	
	//GET
	@GetMapping("/meetings/{accountid}")
	public List<MeetingResponse> list(@PathVariable String accountid) {
		Optional<Account> oq = this.accountRepository.findByAccountid(accountid);
		Account q = oq.get();
		List<Meeting> meetingList = q.getMeetingList();
		List<MeetingResponse> returnList = new ArrayList<>();
		for(int i = 0; i < meetingList.size(); i++) {
			Meeting meeting = meetingList.get(i);
			returnList.add(MeetingResponse.from(meeting));
		}
		return returnList;
	}
	
	@GetMapping("/meetings/title/{accountid}/{subTitle}")
	public List<MeetingResponse> list2(@PathVariable String accountid , @PathVariable String subTitle) {
		List<Meeting> meetings = this.meetingRepository.findByTitleLike("%"+subTitle+"%");
		List<MeetingResponse> returnList = new ArrayList<>();
		for(int i = 0; i < meetings.size(); i++) {
			Meeting meeting = meetings.get(i);
			if(meeting.getAccount().getAccountid().equals(accountid)) {
				returnList.add(MeetingResponse.from(meeting));
			}
		}
		return returnList;
	}
	
	
	//POST
	@PostMapping("/meeting/create/{accountid}")
	public ResponseEntity<MeetingResponse> create(@PathVariable String accountid, @RequestBody Map<String,String> requestData) {
		Optional<Account> oq = this.accountRepository.findByAccountid(accountid);
		Account q = oq.get();
	    Meeting meeting = new Meeting();
	    meeting.setAccount(q);
	    meeting.setCreateDate(LocalDateTime.now());
	    requestData.forEach((key, value) -> {
			if("data".equals(key)) {
				meeting.setData(value);
			}
			if("summary_data".equals(key)) {
				meeting.setSummary_data(value);
			}
			if("title".equals(key)) {
				meeting.setTitle(value);
			}
			if("category".equals(key)) {
					meeting.setCategory(value);
			}
			if("date".equals(key)) {
				meeting.setDate(value);
			}
	    });
		Meeting createdMeeting = this.meetingRepository.save(meeting);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(MeetingResponse.from(createdMeeting));
	}
	
	
	// PATCH
	@PatchMapping("/meeting/patch/{id}")
	public ResponseEntity<Meeting> update(@PathVariable int id, @RequestBody Map<String,String> requestData) {
		Meeting target = meetingRepository.findById(id).orElse(null);		
		if(target == null ) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	    Meeting meeting = new Meeting();	    
	    requestData.forEach((key, value) -> {
			if("data".equals(key)) {
				meeting.setData(value);
			}
			if("title".equals(key)) {
				meeting.setTitle(value);
			}
			if("category".equals(key)) {
				meeting.setCategory(value);
			}
			if("date".equals(key)) {
				meeting.setDate(value);
			}
	    });	
	    target.patch(meeting);
		Meeting updated = meetingRepository.save(target);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	
	//DELETE
	@DeleteMapping("/meeting/delete/{id}")
	public ResponseEntity<Meeting> delete(@PathVariable int id) {
		Meeting target = meetingRepository.findById(id).orElse(null);	// id대상이 없으면 null 반환	
		if(target == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		meetingRepository.delete(target);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
}
