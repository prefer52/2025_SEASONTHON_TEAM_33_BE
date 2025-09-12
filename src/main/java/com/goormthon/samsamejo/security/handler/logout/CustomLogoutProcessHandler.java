package com.goormthon.samsamejo.security.handler.logout;

import com.goormthon.samsamejo.security.info.UserClaims;
import com.goormthon.samsamejo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutProcessHandler implements LogoutHandler {

    private final AuthService authService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("customLogoutProcessHandler called");

        Long userId = (Long) authentication.getPrincipal();
        authService.deleteUserTokens(userId);
    }
}
