package com.site.transmate.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class FirebaseAuthenticationInterceptor implements HandlerInterceptor {
    public static final String USER_ID_ATTRIBUTE = "authenticatedUserId";
    private final FirebaseTokenVerifier tokenVerifier;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Firebase 인증 토큰이 필요합니다.");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isEmpty()) throw new UnauthorizedException("Firebase 인증 토큰이 필요합니다.");
        request.setAttribute(USER_ID_ATTRIBUTE, tokenVerifier.verify(token));
        return true;
    }
}
