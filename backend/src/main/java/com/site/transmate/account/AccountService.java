package com.site.transmate.account;

import com.site.transmate.account.dto.AccountCreateRequest;
import com.site.transmate.account.dto.AccountResponse;
import com.site.transmate.api.ResourceNotFoundException;
import com.site.transmate.auth.OwnershipGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final OwnershipGuard ownershipGuard;

    public AccountResponse get(String userId, String accountId) {
        ownershipGuard.requireOwner(userId, accountId);
        Account account = accountRepository.findByAccountid(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
        return AccountResponse.from(account);
    }

    public void create(String userId, AccountCreateRequest request) {
        ownershipGuard.requireOwner(userId, request.accountid());
        Account account = new Account();
        account.setAccountid(request.accountid());
        account.setName(request.name());
        account.setPassword(request.password());
        accountRepository.save(account);
    }
}
