package com.site.ssk.account;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountService {

	private final AccountRepository accountRepository;

    public List<Account> getList() {
        return this.accountRepository.findAll();
    }
    
}
