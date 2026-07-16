package com.site.transmate.schedule;

import java.util.List;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountRepository;
import com.site.transmate.api.ResourceNotFoundException;
import com.site.transmate.schedule.dto.ScheduleRequest;
import com.site.transmate.schedule.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AccountRepository accountRepository;

    public List<ScheduleResponse> getAll(String accountId) {
        return findAccount(accountId).getScheduleList().stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    public List<ScheduleResponse> searchByDate(String accountId, String date) {
        return scheduleRepository.findByDateLike("%" + date + "%").stream()
                .filter(schedule -> schedule.getAccount().getAccountid().equals(accountId))
                .map(ScheduleResponse::from)
                .toList();
    }

    public void create(String accountId, ScheduleRequest request) {
        Schedule schedule = new Schedule();
        schedule.setAccount(findAccount(accountId));
        apply(schedule, request);
        scheduleRepository.save(schedule);
    }

    public void update(int id, ScheduleRequest request) {
        Schedule schedule = findSchedule(id);
        apply(schedule, request);
        scheduleRepository.save(schedule);
    }

    public void delete(int id) {
        scheduleRepository.delete(findSchedule(id));
    }

    private Account findAccount(String accountId) {
        return accountRepository.findByAccountid(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
    }

    private Schedule findSchedule(int id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 일정입니다."));
    }

    private void apply(Schedule schedule, ScheduleRequest request) {
        if (request.data() != null) schedule.setData(request.data());
        if (request.title() != null) schedule.setTitle(request.title());
        if (request.date() != null) schedule.setDate(request.date());
        if (request.time() != null) schedule.setTime(request.time());
    }
}
