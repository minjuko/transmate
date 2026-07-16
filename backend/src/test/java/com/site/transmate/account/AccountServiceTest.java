package com.site.transmate.account;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.site.transmate.account.dto.AccountCreateRequest;
import com.site.transmate.auth.OwnershipGuard;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class AccountServiceTest {

    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final OwnershipGuard ownershipGuard = mock(OwnershipGuard.class);
    private final AccountService accountService =
            new AccountService(accountRepository, ownershipGuard);

    @Test
    void repeatedCreateDoesNotOverwriteAnExistingAccount() {
        when(accountRepository.existsById("firebase-user-id")).thenReturn(true);

        accountService.create(
                "firebase-user-id",
                new AccountCreateRequest("firebase-user-id", "new name")
        );

        verify(ownershipGuard).requireOwner("firebase-user-id", "firebase-user-id");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void concurrentDuplicateInsertIsTreatedAsASuccessfulRetry() {
        when(accountRepository.existsById("firebase-user-id")).thenReturn(false, true);
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThatCode(() -> accountService.create(
                "firebase-user-id",
                new AccountCreateRequest("firebase-user-id", "user")
        )).doesNotThrowAnyException();
    }

    @Test
    void unrelatedIntegrityViolationIsNotSuppressed() {
        when(accountRepository.existsById("firebase-user-id")).thenReturn(false);
        DataIntegrityViolationException failure =
                new DataIntegrityViolationException("invalid account");
        when(accountRepository.save(any(Account.class))).thenThrow(failure);

        assertThatThrownBy(() -> accountService.create(
                "firebase-user-id",
                new AccountCreateRequest("firebase-user-id", "user")
        )).isSameAs(failure);
    }
}
