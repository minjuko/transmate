package com.site.ssk.meeting;

import java.time.LocalDateTime;

import com.site.ssk.account.Account;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity 
public class Meeting {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(length = 20)
	private String title;
	
    @Column(columnDefinition = "TEXT")
    private String data;
    
    @Column(columnDefinition = "TEXT")
    private String summary_data;

    private LocalDateTime createDate;
    
    @Column(length = 20)
	private String category;

    @ManyToOne
    private Account account;

	public void patch(Meeting meeting) {
		if(meeting.title!=null)
			this.title = meeting.title;
		if(meeting.data!=null)
			this.data = meeting.data;
		if(meeting.category!=null)
			this.category = meeting.category;
		
	}
}