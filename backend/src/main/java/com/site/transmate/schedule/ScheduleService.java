package com.site.transmate.schedule;

import java.util.List;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountRepository;
import com.site.transmate.api.ResourceNotFoundException;
import com.site.transmate.auth.OwnershipGuard;
import com.site.transmate.schedule.dto.ScheduleRequest;
import com.site.transmate.schedule.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AccountRepository accountRepository;
    private final OwnershipGuard ownershipGuard;

    public List<ScheduleResponse> getAll(String userId, String accountId) {
        ownershipGuard.requireOwner(userId, accountId);
        return findAccount(accountId).getScheduleList().stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    public List<ScheduleResponse> searchByDate(String userId, String accountId, String date) {
        ownershipGuard.requireOwner(userId, accountId);
        return scheduleRepository.findByDateLike("%" + date + "%").stream()
                .filter(schedule -> schedule.getAccount().getAccountid().equals(accountId))
                .map(ScheduleResponse::from)
                .toList();
    }

    public void create(String userId, String accountId, ScheduleRequest request) {
        ownershipGuard.requireOwner(userId, accountId);
        Schedule schedule = new Schedule();
        schedule.setAccount(findAccount(accountId));
        apply(schedule, request);
        scheduleRepository.save(schedule);
    }

    public void update(String userId, int id, ScheduleRequest request) {
        Schedule schedule = findSchedule(id);
        ownershipGuard.requireOwner(userId, schedule.getAccount().getAccountid());
        apply(schedule, request);
        scheduleRepository.save(schedule);
    }

    public void delete(String userId, int id) {
        Schedule schedule = findSchedule(id);
        ownershipGuard.requireOwner(userId, schedule.getAccount().getAccountid());
        scheduleRepository.delete(schedule);
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
