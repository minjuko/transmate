package com.site.ssk.meeting;

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

import com.site.ssk.account.Account;
import com.site.ssk.account.AccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController	// RestApi용 컨트롤러, 데이터(JSON)반환
public class MeetingController {
	
	@Autowired
    private final MeetingRepository meetingRepository;
	
	@Autowired
    private final AccountRepository accountRepository;
	
	//GET
	@GetMapping("/meetings/{accountid}")
	public List<Meeting> list(@PathVariable String accountid) {
		Optional<Account> oq = this.accountRepository.findByAccountid(accountid);
		Account q = oq.get();
		List<Meeting> meetingList = q.getMeetingList();
		List<Meeting> returnList = new ArrayList<Meeting>();
		for(int i = 0; i < meetingList.size(); i++) {
			Meeting meeting = meetingList.get(i);
			Meeting data = new Meeting();
			data.setTitle(meeting.getTitle());
			data.setData(meeting.getData());
			data.setSummary_data(meeting.getSummary_data());
			data.setId(meeting.getId());
			data.setCategory(meeting.getCategory());
			data.setCreateDate(meeting.getCreateDate());
			returnList.add(data);
		}
		return returnList;
	}
	
	@GetMapping("/meetings/title/{accountid}/{subTitle}")
	public List<Meeting> list2(@PathVariable String accountid , @PathVariable String subTitle) {
		List<Meeting> meetings = this.meetingRepository.findByTitleLike("%"+subTitle+"%");
		List<Meeting> returnList = new ArrayList<Meeting>();
		for(int i = 0; i < meetings.size(); i++) {
			Meeting meeting = meetings.get(i);
			if(meeting.getAccount().getAccountid().equals(accountid)) {
				Meeting data = new Meeting();
				data.setTitle(meeting.getTitle());
				data.setData(meeting.getData());
				data.setSummary_data(meeting.getSummary_data());
				data.setId(meeting.getId());
				data.setCategory(meeting.getCategory());
				data.setCreateDate(meeting.getCreateDate());
				returnList.add(data);
			}
		}
		return returnList;
	}
	
	
	//POST
	@PostMapping("/meeting/create/{accountid}")
	public void create(@PathVariable String accountid, @RequestBody Map<String,String> requestData) {
		Optional<Account> oq = this.accountRepository.findByAccountid(accountid);
		Account q = oq.get();
	    Meeting meeting = new Meeting();
	    meeting.setAccount(q);
	    meeting.setCreateDate(LocalDateTime.now());
	    requestData.forEach((key, value) -> {
			if(key == "data") {
				meeting.setData(value);
			}
			if(key == "summary_data") {
				meeting.setSummary_data(value);
			}
			if(key == "title") {
				meeting.setTitle(value);
			}
			if(key == "category") {
					meeting.setCategory(value);
			}
	    });
		this.meetingRepository.save(meeting);
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
			if(key == "data") {
				meeting.setData(value);
			}
			if(key == "title") {
				meeting.setTitle(value);
			}
			if(key == "category") {
				meeting.setCategory(value);
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
