package com.site.transmate.meeting;

import java.util.List;

import com.site.transmate.meeting.dto.MeetingRequest;
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
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping("/meetings/{accountid}")
    public List<MeetingResponse> list(@PathVariable String accountid) {
        return meetingService.getAll(accountid);
    }

    @GetMapping("/meetings/title/{accountid}/{subTitle}")
    public List<MeetingResponse> search(
            @PathVariable String accountid,
            @PathVariable String subTitle
    ) {
        return meetingService.searchByTitle(accountid, subTitle);
    }

    @PostMapping("/meeting/create/{accountid}")
    public ResponseEntity<MeetingResponse> create(
            @PathVariable String accountid,
            @RequestBody MeetingRequest request
    ) {
        return ResponseEntity.status(201).body(meetingService.create(accountid, request));
    }

    @PatchMapping("/meeting/patch/{id}")
    public ResponseEntity<Void> update(
            @PathVariable int id,
            @RequestBody MeetingRequest request
    ) {
        meetingService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/meeting/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        meetingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
