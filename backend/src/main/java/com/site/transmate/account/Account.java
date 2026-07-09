package come.site.transmate.account;

import java.util.List;

import come.site.transmate.meeting.Meeting;
import come.site.transmate.schedule.schedule;

import java.time.LocalDateTime;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Account {
	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Id
	@Column(unique = true)
    private String accountid;

    @Column(length = 20)
    private String password;

    @Column(length = 20)
    private String name;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Meeting> meetingList;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<schedule> scheduleList;
}
