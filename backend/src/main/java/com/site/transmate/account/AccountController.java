package com.site.transmate.account;

import com.site.transmate.account.dto.AccountCreateRequest;
import com.site.transmate.account.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import com.site.transmate.auth.FirebaseAuthenticationInterceptor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account/{accountid}")
    public AccountResponse account(
            @PathVariable String accountid,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        return accountService.get(userId, accountid);
    }

    @PostMapping("/account/create")
    public ResponseEntity<Void> create(
            @RequestBody AccountCreateRequest request,
            @RequestAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE) String userId
    ) {
        accountService.create(userId, request);
        return ResponseEntity.status(201).build();
    }
}
