package com.site.transmate.account.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountCreateRequest(
        @NotBlank(message = "accountid는 필수입니다.") String accountid,
        String name,
        String password
) {
}
