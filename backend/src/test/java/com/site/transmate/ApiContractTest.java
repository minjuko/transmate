package com.site.transmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.time.LocalDateTime;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountController;
import com.site.transmate.account.AccountRepository;
import com.site.transmate.meeting.MeetingController;
import com.site.transmate.meeting.Meeting;
import com.site.transmate.meeting.MeetingRepository;
import com.site.transmate.schedule.Schedule;
import com.site.transmate.schedule.ScheduleController;
import com.site.transmate.schedule.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@WebMvcTest({
        MainController.class,
        AccountController.class,
        MeetingController.class,
        ScheduleController.class
})
class ApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private MeetingRepository meetingRepository;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @Test
    void translateUsesPostAsCalledByMobile() {
        boolean hasPostTranslate = handlerMapping.getHandlerMethods().keySet().stream()
                .anyMatch(mapping -> mapping.getPatternValues().contains("/translate")
                        && mapping.getMethodsCondition().getMethods().contains(RequestMethod.POST));

        assertThat(hasPostTranslate).isTrue();
    }

    @Test
    void accountCanBeCreatedWithMobilePayload() throws Exception {
        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountid\":\"firebase-user-id\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void meetingsCanBeLoadedForMobile() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setId(7);
        meeting.setTitle("주간 회의");
        meeting.setData("회의 내용");
        meeting.setCategory("개발");
        meeting.setDate("2026.07.16");

        Account account = new Account();
        account.setAccountid("firebase-user-id");
        account.setMeetingList(List.of(meeting));
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));

        mockMvc.perform(get("/meetings/firebase-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].meetingid").value(7))
                .andExpect(jsonPath("$[0].title").value("주간 회의"))
                .andExpect(jsonPath("$[0].data").value("회의 내용"))
                .andExpect(jsonPath("$[0].category").value("개발"))
                .andExpect(jsonPath("$[0].date").value("2026.07.16"))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].createDate").doesNotExist());
    }

    @Test
    void meetingCreationStoresDateFromMobilePayload() throws Exception {
        Account account = new Account();
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));
        when(meetingRepository.save(any(Meeting.class))).thenAnswer(invocation -> {
            Meeting savedMeeting = invocation.getArgument(0);
            savedMeeting.setId(11);
            return savedMeeting;
        });

        mockMvc.perform(post("/meeting/create/firebase-user-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "주간 회의",
                                  "category": "개발",
                                  "data": "회의 내용",
                                  "date": "2026.07.16"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meetingid").value(11))
                .andExpect(jsonPath("$.title").value("주간 회의"))
                .andExpect(jsonPath("$.date").value("2026.07.16"));

        ArgumentCaptor<Meeting> meetingCaptor = ArgumentCaptor.forClass(Meeting.class);
        verify(meetingRepository).save(meetingCaptor.capture());
        assertThat(meetingCaptor.getValue().getDate()).isEqualTo("2026.07.16");
    }

    @Test
    void legacyMeetingUsesCreateDateWhenDateIsMissing() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setId(8);
        meeting.setCreateDate(LocalDateTime.of(2026, 7, 15, 10, 30));

        Account account = new Account();
        account.setMeetingList(List.of(meeting));
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));

        mockMvc.perform(get("/meetings/firebase-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].meetingid").value(8))
                .andExpect(jsonPath("$[0].date").value("2026-07-15"));
    }

    @Test
    void schedulesUseFieldsConsumedByMobile() throws Exception {
        Schedule schedule = new Schedule();
        schedule.setId(1);
        schedule.setTitle("회의");
        schedule.setDate("2026-07-16");
        schedule.setTime("14:30");

        Account account = new Account();
        account.setAccountid("firebase-user-id");
        account.setScheduleList(List.of(schedule));
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));

        mockMvc.perform(get("/schedules/firebase-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("회의"))
                .andExpect(jsonPath("$[0].date").value("2026-07-16"))
                .andExpect(jsonPath("$[0].time").value("14:30"));
    }

    @Test
    void scheduleCreationStoresTimeFromMobilePayload() throws Exception {
        Account account = new Account();
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));

        mockMvc.perform(post("/schedule/create/firebase-user-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "주간 회의",
                                  "date": "2026-07-16",
                                  "time": "14:30"
                                }
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<Schedule> scheduleCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(scheduleRepository).save(scheduleCaptor.capture());
        assertThat(scheduleCaptor.getValue().getTime()).isEqualTo("14:30");
    }

    @Test
    void schedulePatchUpdatesTimeFromMobilePayload() throws Exception {
        Schedule schedule = new Schedule();
        schedule.setId(3);
        schedule.setTime("09:00");
        when(scheduleRepository.findById(3))
                .thenReturn(java.util.Optional.of(schedule));

        mockMvc.perform(patch("/schedule/patch/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"time\":\"10:15\"}"))
                .andExpect(status().isNoContent());

        assertThat(schedule.getTime()).isEqualTo("10:15");
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void missingAccountReturnsStandardNotFoundError() throws Exception {
        when(accountRepository.findByAccountid("missing-user"))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/schedules/missing-user"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."))
                .andExpect(jsonPath("$.path").value("/schedules/missing-user"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void missingSchedulePatchReturnsStandardNotFoundError() throws Exception {
        when(scheduleRepository.findById(999))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(patch("/schedule/patch/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"time\":\"10:15\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 일정입니다."))
                .andExpect(jsonPath("$.path").value("/schedule/patch/999"));
    }
}
