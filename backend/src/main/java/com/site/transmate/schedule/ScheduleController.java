package com.site.transmate.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;

    private final AccountRepository accountRepository;

    // 일정 전체 조회
    @GetMapping("/schedules/{accountid}")
    public List<Schedule> getSchedules(@PathVariable String accountid) {

        Account account = accountRepository.findByAccountid(accountid)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Schedule> schedules = account.getScheduleList();

        List<Schedule> responseSchedules = new ArrayList<>();

        for (Schedule schedule : schedules) {

            Schedule responseSchedule = new Schedule();

            responseSchedule.setId(schedule.getId());
            responseSchedule.setTitle(schedule.getTitle());
            responseSchedule.setData(schedule.getData());
            responseSchedule.setDate(schedule.getDate());
            responseSchedule.setTime(schedule.getTime());

            responseSchedules.add(responseSchedule);
        }

        return responseSchedules;
    }

    @GetMapping("/schedules/date/{accountid}/{subDate}")
    public List<Schedule> getSchedulesByDate(
            @PathVariable String accountid,
            @PathVariable String subDate
    ) {
        List<Schedule> matchedSchedules =
                scheduleRepository.findByDateLike("%" + subDate + "%");

        List<Schedule> responseSchedules = new ArrayList<>();

        for (Schedule schedule : matchedSchedules) {

            if (schedule.getAccount()
                    .getAccountid()
                    .equals(accountid)) {

                Schedule responseSchedule = new Schedule();

                responseSchedule.setId(schedule.getId());
                responseSchedule.setTitle(schedule.getTitle());
                responseSchedule.setData(schedule.getData());
                responseSchedule.setDate(schedule.getDate());
                responseSchedule.setTime(schedule.getTime());

                responseSchedules.add(responseSchedule);
            }
        }

        return responseSchedules;
    }

    // 일정 생성
    @PostMapping("/schedule/create/{accountid}")
    public void createSchedule(
            @PathVariable String accountid,
            @RequestBody Map<String, String> requestData
    ) {
        Account account = accountRepository.findByAccountid(accountid)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Schedule schedule = new Schedule();
        schedule.setAccount(account);

        requestData.forEach((key, value) -> {
            if ("data".equals(key)) {
                schedule.setData(value);
            }

            if ("date".equals(key)) {
                schedule.setDate(value);
            }

            if ("title".equals(key)) {
                schedule.setTitle(value);
            }

            if ("time".equals(key)) {
                schedule.setTime(value);
            }
        });

        scheduleRepository.save(schedule);
    }

    // 일정 수정
    @PatchMapping("/schedule/patch/{id}")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable int id,
            @RequestBody Map<String, String> requestData
    ) {
        Schedule targetSchedule =
                scheduleRepository.findById(id).orElse(null);

        if (targetSchedule == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Schedule patchSchedule = new Schedule();

        requestData.forEach((key, value) -> {
            if ("data".equals(key)) {
                patchSchedule.setData(value);
            }

            if ("title".equals(key)) {
                patchSchedule.setTitle(value);
            }

            if ("date".equals(key)) {
                patchSchedule.setDate(value);
            }

            if ("time".equals(key)) {
                patchSchedule.setTime(value);
            }
        });

        targetSchedule.patch(patchSchedule);
        scheduleRepository.save(targetSchedule);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    // 일정 삭제
    @DeleteMapping("/schedule/delete/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable int id
    ) {
        Schedule targetSchedule =
                scheduleRepository.findById(id).orElse(null);

        if (targetSchedule == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        scheduleRepository.delete(targetSchedule);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
