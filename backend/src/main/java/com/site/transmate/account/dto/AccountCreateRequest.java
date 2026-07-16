package com.site.transmate.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountCreateRequest(
        @NotBlank(message = "accountid는 필수입니다.")
        @Size(max = 255, message = "accountid는 255자 이하여야 합니다.")
        String accountid,
        @Size(max = 20, message = "name은 20자 이하여야 합니다.") String name
) {
}
