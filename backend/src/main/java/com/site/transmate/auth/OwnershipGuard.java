package com.site.transmate.auth;

import org.springframework.stereotype.Component;

@Component
public class OwnershipGuard {
    public void requireOwner(String authenticatedUserId, String ownerId) {
        if (!authenticatedUserId.equals(ownerId)) {
            throw new ForbiddenException("다른 사용자의 데이터에 접근할 수 없습니다.");
        }
    }
}
