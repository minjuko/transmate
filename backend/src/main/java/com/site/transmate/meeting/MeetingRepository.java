package come.site.transmate.meeting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import come.site.transmate.account.Account;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
	List<Meeting> findByAccountId(String accountid);
	List<Meeting> findByTitleLike(String title);
	
}