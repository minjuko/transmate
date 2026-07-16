package com.site.transmate.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class FirebaseAuthenticationInterceptorTest {

    private final FirebaseTokenVerifier tokenVerifier = mock(FirebaseTokenVerifier.class);
    private final FirebaseAuthenticationInterceptor interceptor =
            new FirebaseAuthenticationInterceptor(tokenVerifier);

    @Test
    void authenticatesBearerTokenAndStoresUserId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        when(tokenVerifier.verify("valid-token")).thenReturn("firebase-user-id");

        boolean result = interceptor.preHandle(
                request,
                new MockHttpServletResponse(),
                new Object()
        );

        assertThat(result).isTrue();
        assertThat(request.getAttribute(FirebaseAuthenticationInterceptor.USER_ID_ATTRIBUTE))
                .isEqualTo("firebase-user-id");
    }

    @Test
    void rejectsRequestWithoutBearerToken() {
        assertThatThrownBy(() -> interceptor.preHandle(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                new Object()
        )).isInstanceOf(UnauthorizedException.class)
                .hasMessage("Firebase 인증 토큰이 필요합니다.");
    }
}
