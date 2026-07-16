package com.site.transmate.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FirebaseTokenVerifier {
    private final FirebaseAuth firebaseAuth;

    public String verify(String idToken) {
        try {
            return firebaseAuth.verifyIdToken(idToken).getUid();
        } catch (FirebaseAuthException exception) {
            throw new UnauthorizedException("유효하지 않은 Firebase 인증 토큰입니다.");
        }
    }
}
