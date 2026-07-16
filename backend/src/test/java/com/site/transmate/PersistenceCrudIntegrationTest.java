package com.site.transmate;

import static org.assertj.core.api.Assertions.assertThat;

import com.site.transmate.account.Account;
import com.site.transmate.account.AccountRepository;
import com.site.transmate.auth.OwnershipGuard;
import com.site.transmate.meeting.MeetingResponse;
import com.site.transmate.meeting.MeetingService;
import com.site.transmate.meeting.dto.MeetingRequest;
import com.site.transmate.schedule.ScheduleService;
import com.site.transmate.schedule.dto.ScheduleRequest;
import com.site.transmate.schedule.dto.ScheduleResponse;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:persistence-crud;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Import({MeetingService.class, ScheduleService.class, OwnershipGuard.class})
class PersistenceCrudIntegrationTest {

    private static final String USER_ID = "firebase-user-id";
    private static final String OTHER_USER_ID = "other-firebase-user-id";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void createAccounts() {
        createAccount(USER_ID, "user");
        createAccount(OTHER_USER_ID, "other user");
        entityManager.flush();
        entityManager.clear();
    }

    private void createAccount(String accountId, String name) {
        Account account = new Account();
        account.setAccountid(accountId);
        account.setName(name);
        accountRepository.save(account);
    }

    @Test
    void createsReadsUpdatesSearchesAndDeletesMeeting() {
        MeetingResponse created = meetingService.create(
                USER_ID,
                USER_ID,
                new MeetingRequest(
                        "original data",
                        "original summary",
                        "weekly meeting",
                        "development",
                        "2026-07-16"
                )
        );
        flushAndClear();

        assertThat(meetingService.getAll(USER_ID, USER_ID))
                .singleElement()
                .satisfies(meeting -> {
                    assertThat(meeting.meetingid()).isEqualTo(created.meetingid());
                    assertThat(meeting.title()).isEqualTo("weekly meeting");
                    assertThat(meeting.summaryData()).isEqualTo("original summary");
                });

        meetingService.update(
                USER_ID,
                created.meetingid(),
                new MeetingRequest("updated data", null, "updated meeting", null, null)
        );
        meetingService.create(
                OTHER_USER_ID,
                OTHER_USER_ID,
                new MeetingRequest(
                        "other data",
                        null,
                        "updated other",
                        null,
                        "2026-07-16"
                )
        );
        flushAndClear();

        List<MeetingResponse> searchResult =
                meetingService.searchByTitle(USER_ID, USER_ID, "updated");
        assertThat(searchResult)
                .singleElement()
                .satisfies(meeting -> {
                    assertThat(meeting.data()).isEqualTo("updated data");
                    assertThat(meeting.category()).isEqualTo("development");
                    assertThat(meeting.date()).isEqualTo("2026-07-16");
                });

        meetingService.delete(USER_ID, created.meetingid());
        flushAndClear();

        assertThat(meetingService.getAll(USER_ID, USER_ID)).isEmpty();
    }

    @Test
    void createsReadsUpdatesSearchesAndDeletesSchedule() {
        scheduleService.create(
                USER_ID,
                USER_ID,
                new ScheduleRequest(
                        "original data",
                        "customer call",
                        "2026-07-16",
                        "09:30"
                )
        );
        flushAndClear();

        ScheduleResponse created = scheduleService.getAll(USER_ID, USER_ID).get(0);
        assertThat(created.title()).isEqualTo("customer call");
        assertThat(created.time()).isEqualTo("09:30");

        scheduleService.update(
                USER_ID,
                created.id(),
                new ScheduleRequest(null, "updated call", null, "10:45")
        );
        scheduleService.create(
                OTHER_USER_ID,
                OTHER_USER_ID,
                new ScheduleRequest(
                        "other data",
                        "other call",
                        "2026-07-16",
                        "10:45"
                )
        );
        flushAndClear();

        assertThat(scheduleService.searchByDate(USER_ID, USER_ID, "2026-07"))
                .singleElement()
                .satisfies(schedule -> {
                    assertThat(schedule.title()).isEqualTo("updated call");
                    assertThat(schedule.data()).isEqualTo("original data");
                    assertThat(schedule.time()).isEqualTo("10:45");
                });

        scheduleService.delete(USER_ID, created.id());
        flushAndClear();

        assertThat(scheduleService.getAll(USER_ID, USER_ID)).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
