package com.site.transmate.account;

import com.site.transmate.account.dto.AccountCreateRequest;
import com.site.transmate.account.dto.AccountResponse;
import com.site.transmate.api.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse get(String accountId) {
        Account account = accountRepository.findByAccountid(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
        return AccountResponse.from(account);
    }

    public void create(AccountCreateRequest request) {
        Account account = new Account();
        account.setAccountid(request.accountid());
        account.setName(request.name());
        account.setPassword(request.password());
        accountRepository.save(account);
    }
}
