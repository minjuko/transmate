package com.site.transmate.meeting;

import java.time.LocalDateTime;
import java.util.List;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountRepository;
import com.site.transmate.api.ResourceNotFoundException;
import com.site.transmate.meeting.dto.MeetingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final AccountRepository accountRepository;

    public List<MeetingResponse> getAll(String accountId) {
        Account account = findAccount(accountId);
        return account.getMeetingList().stream().map(MeetingResponse::from).toList();
    }

    public List<MeetingResponse> searchByTitle(String accountId, String title) {
        return meetingRepository.findByTitleLike("%" + title + "%").stream()
                .filter(meeting -> meeting.getAccount().getAccountid().equals(accountId))
                .map(MeetingResponse::from)
                .toList();
    }

    public MeetingResponse create(String accountId, MeetingRequest request) {
        Meeting meeting = new Meeting();
        meeting.setAccount(findAccount(accountId));
        meeting.setCreateDate(LocalDateTime.now());
        apply(meeting, request);
        return MeetingResponse.from(meetingRepository.save(meeting));
    }

    public void update(int id, MeetingRequest request) {
        Meeting meeting = findMeeting(id);
        apply(meeting, request);
        meetingRepository.save(meeting);
    }

    public void delete(int id) {
        meetingRepository.delete(findMeeting(id));
    }

    private Account findAccount(String accountId) {
        return accountRepository.findByAccountid(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
    }

    private Meeting findMeeting(int id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회의입니다."));
    }

    private void apply(Meeting meeting, MeetingRequest request) {
        if (request.data() != null) meeting.setData(request.data());
        if (request.summaryData() != null) meeting.setSummary_data(request.summaryData());
        if (request.title() != null) meeting.setTitle(request.title());
        if (request.category() != null) meeting.setCategory(request.category());
        if (request.date() != null) meeting.setDate(request.date());
    }
}
