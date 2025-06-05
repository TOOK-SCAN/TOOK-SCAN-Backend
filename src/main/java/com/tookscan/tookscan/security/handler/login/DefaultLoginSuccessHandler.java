package com.tookscan.tookscan.security.handler.login;

import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.utility.HttpServletUtil;
import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.application.usecase.LoginByDefaultUseCase;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DefaultLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginByDefaultUseCase loginByDefaultUseCase;

    private final JsonWebTokenUtil jwtUtil;
    private final HttpServletUtil httpServletUtil;

    private static final String TRUE = "true";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String rememberMe = request.getParameter(Constants.REMEMBER_ME);

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        DefaultJsonWebTokenDto jsonWebTokenDto = jwtUtil.generateDefaultJsonWebTokens(
                principal.getId(),
                principal.getRole()
        );

        loginByDefaultUseCase.execute(principal, jsonWebTokenDto);

        if (rememberMe != null && rememberMe.equals(TRUE)) {
            httpServletUtil.onSuccessBodyResponseWithJWTCookieRememberMeTrue(response, jsonWebTokenDto);
        } else {
            httpServletUtil.onSuccessBodyResponseWithJWTCookie(response, jsonWebTokenDto);
        }
    }
}
