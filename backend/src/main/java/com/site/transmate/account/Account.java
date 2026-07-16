package com.site.transmate.account;

import java.util.List;

import com.site.transmate.meeting.Meeting;
import com.site.transmate.schedule.Schedule;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Account {
	
    @Column(insertable = false, updatable = false)
    private Integer id;
	
	@Id
	@Column(unique = true)
    private String accountid;

    @Column(length = 20)
    private String name;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Meeting> meetingList;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Schedule> scheduleList;
}
