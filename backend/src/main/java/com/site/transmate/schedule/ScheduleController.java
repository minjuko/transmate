package com.site.transmate.schedule;

import java.util.List;

import com.site.transmate.schedule.dto.ScheduleRequest;
import com.site.transmate.schedule.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import com.site.transmate.auth.FirebaseAuthenticationInterceptor;
import com.site.transmate.api.OnCreate;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.groups.Default;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/schedules/{accountid}")
    public List<ScheduleResponse> getSchedules(@PathVariable String accountid,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId) {
        return scheduleService.getAll(userId, accountid);
    }

    @GetMapping("/schedules/date/{accountid}/{subDate}")
    public List<ScheduleResponse> getSchedulesByDate(
            @PathVariable String accountid,
            @PathVariable String subDate,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        return scheduleService.searchByDate(userId, accountid, subDate);
    }

    @PostMapping("/schedule/create/{accountid}")
    public ResponseEntity<Void> createSchedule(
            @PathVariable String accountid,
            @Validated({Default.class, OnCreate.class}) @RequestBody ScheduleRequest request,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        scheduleService.create(userId, accountid, request);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/schedule/patch/{id}")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable int id,
            @Valid @RequestBody ScheduleRequest request,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        scheduleService.update(userId, id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/schedule/delete/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable int id,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId) {
        scheduleService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
