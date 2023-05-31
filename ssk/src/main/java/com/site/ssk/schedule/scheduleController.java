package com.site.ssk.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.site.ssk.meeting.Meeting;
import com.site.ssk.meeting.MeetingRepository;
import com.site.ssk.schedule.scheduleRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class scheduleController {
	
	@Autowired
    private final scheduleRepository scheduleRepository;
	
	@Autowired
    private final AccountRepository accountRepository;
	
	//GET
	@GetMapping("/schedules/{accountid}")
	public List<schedule> list(@PathVariable String accountid) {
		Optional<Account> oq = this.accountRepository.findByAccountid(accountid);
		Account q = oq.get();
		List<schedule> scheduleList = q.getScheduleList();
		List<schedule> returnList = new ArrayList<schedule>();
		for(int i = 0; i < scheduleList.size(); i++) {
			schedule schedule = scheduleList.get(i);
			schedule data = new schedule();
			data.setTitle(schedule.getTitle());
			data.setData(schedule.getData());
			data.setId(schedule.getId());
			data.setDate(schedule.getDate());
			returnList.add(data);
		}
		return returnList;
	}
	
	@GetMapping("/schedules/date/{accountid}/{subDate}")
	public List<schedule> list2(@PathVariable String accountid , @PathVariable String subDate) {
		List<schedule> schedules = this.scheduleRepository.findByDateLike("%"+subDate+"%");
		List<schedule> returnList = new ArrayList<schedule>();
		for(int i = 0; i < schedules.size(); i++) {
			schedule schedule = schedules.get(i);
			if(schedule.getAccount().getAccountid().equals(accountid)) {
				schedule data = new schedule();
				data.setTitle(schedule.getTitle());
				data.setData(schedule.getData());
				data.setId(schedule.getId());
				data.setDate(schedule.getDate());
				returnList.add(data);
			}
		}
		return returnList;
	}
	
	//POST
	@PostMapping("/schedule/create/{accountid}")
	public void create(@PathVariable String accountid, @RequestBody Map<String,String> requestData) {
		Optional<Account> oq = this.accountRepository.findByAccountid(accountid);
		Account q = oq.get();
		schedule schedule = new schedule();
		schedule.setAccount(q);
	    requestData.forEach((key, value) -> {
			if(key == "data") {
				schedule.setData(value);
			}
			if(key == "date") {
				schedule.setDate(value);
			}
			if(key == "title") {
				schedule.setTitle(value);
			}
		});
		this.scheduleRepository.save(schedule);
	}
	
	// PATCH
	@PatchMapping("schedule/patch/{id}")
	public ResponseEntity<Meeting> update(@PathVariable int id, @RequestBody Map<String,String> requestData) {
		schedule target = scheduleRepository.findById(id).orElse(null);		
		if(target == null ) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		schedule schedule = new schedule();	    
		requestData.forEach((key, value) -> {
			if(key == "data") {
				schedule.setData(value);
			}
			if(key == "title") {
				schedule.setTitle(value);
			}
			if(key == "date") {
				schedule.setDate(value);
			}
		});	
		target.patch(schedule);
		schedule updated = scheduleRepository.save(target);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	//DELETE
		@DeleteMapping("schedule/delete/{id}")
		public ResponseEntity<Meeting> delete(@PathVariable int id) {
			schedule target = scheduleRepository.findById(id).orElse(null);	// id대상이 없으면 null 반환	
			if(target == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			scheduleRepository.delete(target);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		}
	

}
