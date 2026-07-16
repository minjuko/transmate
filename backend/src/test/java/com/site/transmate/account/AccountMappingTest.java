package com.site.transmate.account;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Id;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:account-mapping;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class AccountMappingTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void usesFirebaseAccountIdAsTheJpaIdentifier() throws NoSuchFieldException {
        Field accountId = Account.class.getDeclaredField("accountid");

        assertThat(accountId.isAnnotationPresent(Id.class)).isTrue();
    }

    @Test
    void repositoryLooksUpAccountsByFirebaseUid() {
        Account account = new Account();
        account.setAccountid("firebase-user-id");
        account.setName("user");
        accountRepository.saveAndFlush(account);

        assertThat(accountRepository.findById("firebase-user-id"))
                .get()
                .extracting(Account::getName)
                .isEqualTo("user");
    }
}
