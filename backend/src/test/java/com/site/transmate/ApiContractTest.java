package com.site.transmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;
import java.time.LocalDateTime;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountController;
import com.site.transmate.account.AccountRepository;
import com.site.transmate.account.AccountService;
import com.site.transmate.auth.OwnershipGuard;
import com.site.transmate.auth.FirebaseAuthenticationInterceptor;
import com.site.transmate.meeting.MeetingController;
import com.site.transmate.meeting.Meeting;
import com.site.transmate.meeting.MeetingRepository;
import com.site.transmate.meeting.MeetingService;
import com.site.transmate.schedule.Schedule;
import com.site.transmate.schedule.ScheduleController;
import com.site.transmate.schedule.ScheduleRepository;
import com.site.transmate.schedule.ScheduleService;
import com.site.transmate.translation.TranslateController;
import com.site.transmate.translation.TranslateService;
import com.site.transmate.translation.dto.TranslateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;

@WebMvcTest({
        TranslateController.class,
        AccountController.class,
        MeetingController.class,
        ScheduleController.class
})
@Import({AccountService.class, MeetingService.class, ScheduleService.class,
        OwnershipGuard.class, ApiContractTest.AuthenticatedRequestConfig.class})
@TestPropertySource(properties = "transmate.auth.enabled=false")
class ApiContractTest {

    @TestConfiguration
    static class AuthenticatedRequestConfig {
        @Bean
        MockMvcBuilderCustomizer authenticatedRequest() {
            return builder -> builder.defaultRequest(get("/")
                    .requestAttr(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE,
                            "firebase-user-id"));
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private MeetingRepository meetingRepository;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private TranslateService translateService;

    @Test
    void translateUsesMobileRequestContract() throws Exception {
        when(translateService.translate(any(TranslateRequest.class)))
                .thenReturn("번역 결과");

        mockMvc.perform(post("/translate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "Text": "source text",
                                  "TerminologyNames": "category",
                                  "SourceLanguageCode": "en",
                                  "TargetLanguageCode": "ko"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("번역 결과"));

        ArgumentCaptor<TranslateRequest> requestCaptor =
                ArgumentCaptor.forClass(TranslateRequest.class);
        verify(translateService).translate(requestCaptor.capture());
        assertThat(requestCaptor.getValue().text()).isEqualTo("source text");
        assertThat(requestCaptor.getValue().terminologyNames()).isEqualTo("category");
        assertThat(requestCaptor.getValue().sourceLanguageCode()).isEqualTo("en");
        assertThat(requestCaptor.getValue().targetLanguageCode()).isEqualTo("ko");
    }

    @Test
    void accountCanBeCreatedWithMobilePayload() throws Exception {
        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountid\":\"firebase-user-id\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void repeatedAccountCreationKeepsTheCreatedResponseContract() throws Exception {
        when(accountRepository.existsById("firebase-user-id")).thenReturn(true);

        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountid\":\"firebase-user-id\"}"))
                .andExpect(status().isCreated());

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void legacyPasswordFieldIsIgnoredWhenAccountIsCreated() throws Exception {
        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountid": "firebase-user-id",
                                  "name": "user",
                                  "password": "must-not-be-stored"
                                }
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getAccountid()).isEqualTo("firebase-user-id");
        assertThat(accountCaptor.getValue().getName()).isEqualTo("user");
    }

    @Test
    void accountResponseDoesNotExposeSensitiveOrEntityFields() throws Exception {
        Account account = new Account();
        account.setId(1);
        account.setAccountid("firebase-user-id");
        account.setName("사용자");
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));

        mockMvc.perform(get("/account/firebase-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountid").value("firebase-user-id"))
                .andExpect(jsonPath("$.name").value("사용자"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.meetingList").doesNotExist())
                .andExpect(jsonPath("$.scheduleList").doesNotExist());
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
        Account owner = new Account();
        owner.setAccountid("firebase-user-id");
        schedule.setAccount(owner);
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

        mockMvc.perform(get("/schedules/missing-user")
                        .requestAttr(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE,
                                "missing-user"))
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

    @Test
    void anotherUsersScheduleIsForbidden() throws Exception {
        mockMvc.perform(get("/schedules/another-user"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message")
                        .value("다른 사용자의 데이터에 접근할 수 없습니다."));
    }

    @Test
    void accountCreationRejectsBlankAccountId() throws Exception {
        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountid\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.fieldErrors.accountid")
                        .value("accountid는 필수입니다."));
    }

    @Test
    void accountCreationRejectsNameLongerThanDatabaseColumn() throws Exception {
        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountid": "firebase-user-id",
                                  "name": "123456789012345678901"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value("name은 20자 이하여야 합니다."));
    }

    @Test
    void meetingCreationRejectsTitleLongerThanDatabaseColumn() throws Exception {
        mockMvc.perform(post("/meeting/create/firebase-user-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "123456789012345678901",
                                  "date": "2026-07-16"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title")
                        .value("title은 20자 이하여야 합니다."));
    }

    @Test
    void scheduleCreationRejectsTitleLongerThanDatabaseColumn() throws Exception {
        mockMvc.perform(post("/schedule/create/firebase-user-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "123456789012345678901",
                                  "date": "2026-07-16",
                                  "time": "09:30"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title")
                        .value("title은 20자 이하여야 합니다."));
    }

    @Test
    void meetingPatchRejectsTitleLongerThanDatabaseColumn() throws Exception {
        mockMvc.perform(patch("/meeting/patch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"123456789012345678901\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title")
                        .value("title은 20자 이하여야 합니다."));
    }

    @Test
    void schedulePatchRejectsTitleLongerThanDatabaseColumn() throws Exception {
        mockMvc.perform(patch("/schedule/patch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"123456789012345678901\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title")
                        .value("title은 20자 이하여야 합니다."));
    }

    @Test
    void scheduleCreationRejectsInvalidTime() throws Exception {
        mockMvc.perform(post("/schedule/create/firebase-user-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "주간 회의",
                                  "date": "2026-07-16",
                                  "time": "25:99"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.time")
                        .value("time은 HH:mm 형식이어야 합니다."));
    }

    @Test
    void translateRejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/translate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"Text\":\"\",\"TargetLanguageCode\":\"ko\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.text").value("Text는 필수입니다."))
                .andExpect(jsonPath("$.fieldErrors.sourceLanguageCode")
                        .value("SourceLanguageCode는 필수입니다."));
    }
}
