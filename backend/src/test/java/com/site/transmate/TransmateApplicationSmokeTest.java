package com.site.transmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

import com.amazonaws.services.translate.AmazonTranslate;
import com.google.firebase.FirebaseApp;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:context-smoke;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "transmate.auth.enabled=false"
})
class TransmateApplicationSmokeTest {

    @MockBean
    private AmazonTranslate amazonTranslate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Flyway flyway;

    @Test
    void loadsApplicationContextAndAppliesAllMigrationsWithoutExternalServices() {
        assertThat(applicationContext).isNotNull();
        assertThat(flyway.info().applied()).hasSize(3);
        assertThat(applicationContext.getBeansOfType(FirebaseApp.class)).isEmpty();
        verifyNoInteractions(amazonTranslate);
    }
}
