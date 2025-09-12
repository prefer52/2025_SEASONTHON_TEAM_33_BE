package com.goormthon.samsamejo.security.handler.login;

import com.goormthon.samsamejo.dto.response.security.JwtDto;
import com.goormthon.samsamejo.security.info.UserPrincipal;
import com.goormthon.samsamejo.service.AuthService;
import com.goormthon.samsamejo.util.CookieUtil;
import com.goormthon.samsamejo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.goormthon.samsamejo.constant.Constant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.cookie-domain}")
    private String cookieDomain;
    @Value("${spring.security.login.success-redirect-uri}")
    private String loginSuccessRedirectUri;
    @Value("${spring.security.login.not-registered-redirect-uri}")
    private String NotRegisteredRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException {
        log.info("oAuth2LoginSuccessHandler called");
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        JwtDto jwtDto = jwtUtil.generateTokens(principal.getId(), principal.getERole());
        authService.createUserTokens(jwtDto.refreshToken(), principal.getId());
        CookieUtil.createCookie(response, ACCESS_TOKEN, jwtDto.accessToken(), cookieDomain, TOKEN_COOKIE_PATH, jwtDto.accessTokenExpirationTime().intValue());
        CookieUtil.createSecureCookie(response, REFRESH_TOKEN, jwtDto.refreshToken(), cookieDomain, TOKEN_COOKIE_PATH, (int) (jwtDto.refreshTokenExpirationTime() / 1000));

        response.sendRedirect(principal.isRegistered() ? loginSuccessRedirectUri : NotRegisteredRedirectUri);
    }
}
