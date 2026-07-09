package come.site.transmate.schedule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import come.site.transmate.account.Account;
import come.site.transmate.meeting.Meeting;

public interface scheduleRepository extends JpaRepository<schedule, Integer> {
	List<schedule> findByAccountId(String accountid);
	List<schedule> findByDateLike(String date);
}
