package com.site.transmate.account.dto;

import com.site.transmate.account.Account;

public record AccountResponse(String accountid, String name) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getAccountid(), account.getName());
    }
}
