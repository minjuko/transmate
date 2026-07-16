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
import org.springframework.web.bind.annotation.RequestAttribute;
import com.site.transmate.auth.FirebaseAuthenticationInterceptor;
import com.site.transmate.api.OnCreate;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.groups.Default;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping("/meetings/{accountid}")
    public List<MeetingResponse> list(@PathVariable String accountid,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId) {
        return meetingService.getAll(userId, accountid);
    }

    @GetMapping("/meetings/title/{accountid}/{subTitle}")
    public List<MeetingResponse> search(
            @PathVariable String accountid,
            @PathVariable String subTitle,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        return meetingService.searchByTitle(userId, accountid, subTitle);
    }

    @PostMapping("/meeting/create/{accountid}")
    public ResponseEntity<MeetingResponse> create(
            @PathVariable String accountid,
            @Validated({Default.class, OnCreate.class}) @RequestBody MeetingRequest request,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        return ResponseEntity.status(201).body(meetingService.create(userId, accountid, request));
    }

    @PatchMapping("/meeting/patch/{id}")
    public ResponseEntity<Void> update(
            @PathVariable int id,
            @Valid @RequestBody MeetingRequest request,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        meetingService.update(userId, id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/meeting/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId) {
        meetingService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
