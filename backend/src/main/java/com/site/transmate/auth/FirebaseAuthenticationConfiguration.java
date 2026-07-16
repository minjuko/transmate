package com.site.transmate.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "transmate.auth.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseAuthenticationConfiguration implements WebMvcConfigurer {
    private final FirebaseTokenVerifier tokenVerifier;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FirebaseAuthenticationInterceptor(tokenVerifier))
                .addPathPatterns("/account/**", "/meeting/**", "/meetings/**",
                        "/schedule/**", "/schedules/**", "/translate");
    }
}
