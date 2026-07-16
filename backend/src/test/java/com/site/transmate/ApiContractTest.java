package com.site.transmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountController;
import com.site.transmate.account.AccountRepository;
import com.site.transmate.meeting.MeetingController;
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
                .andExpect(status().isOk());
    }

    @Test
    void meetingsCanBeLoadedForMobile() throws Exception {
        Account account = new Account();
        account.setAccountid("firebase-user-id");
        account.setMeetingList(List.of());
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));

        mockMvc.perform(get("/meetings/firebase-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void schedulesUseFieldsConsumedByMobile() throws Exception {
        Schedule schedule = new Schedule();
        schedule.setId(1);
        schedule.setTitle("회의");
        schedule.setDate("2026-07-16");

        Account account = new Account();
        account.setAccountid("firebase-user-id");
        account.setScheduleList(List.of(schedule));
        when(accountRepository.findByAccountid("firebase-user-id"))
                .thenReturn(java.util.Optional.of(account));

        mockMvc.perform(get("/schedules/firebase-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("회의"))
                .andExpect(jsonPath("$[0].date").value("2026-07-16"));
    }
}
