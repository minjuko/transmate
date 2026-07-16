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
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/schedules/{accountid}")
    public List<ScheduleResponse> getSchedules(@PathVariable String accountid) {
        return scheduleService.getAll(accountid);
    }

    @GetMapping("/schedules/date/{accountid}/{subDate}")
    public List<ScheduleResponse> getSchedulesByDate(
            @PathVariable String accountid,
            @PathVariable String subDate
    ) {
        return scheduleService.searchByDate(accountid, subDate);
    }

    @PostMapping("/schedule/create/{accountid}")
    public ResponseEntity<Void> createSchedule(
            @PathVariable String accountid,
            @RequestBody ScheduleRequest request
    ) {
        scheduleService.create(accountid, request);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/schedule/patch/{id}")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable int id,
            @RequestBody ScheduleRequest request
    ) {
        scheduleService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/schedule/delete/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable int id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
