package com.tookscan.tookscan.security.handler.login;

import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.HttpServletUtil;
import com.tookscan.tookscan.security.application.dto.OauthJsonWebTokenDto;
import com.tookscan.tookscan.security.application.usecase.LoginOauthUseCase;
import com.tookscan.tookscan.security.info.CustomTemporaryUserPrincipal;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final LoginOauthUseCase loginOauthUseCase;
    private final HttpServletUtil httpServletUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        OauthJsonWebTokenDto oauthJsonWebTokenDto = null;

        String redirectPath = (String) request.getSession().getAttribute("REDIRECT_PATH");

        if (principal instanceof CustomTemporaryUserPrincipal) {
            oauthJsonWebTokenDto = loginOauthUseCase.execute((CustomTemporaryUserPrincipal) principal);
            httpServletUtil.onSuccessRedirectResponseWithJWTCookie(Constants.ADDITIONAL_INFO_INPUT_PATH, response, oauthJsonWebTokenDto);
        } else if (principal instanceof CustomUserPrincipal) {
            oauthJsonWebTokenDto = loginOauthUseCase.execute((CustomUserPrincipal) principal);
            httpServletUtil.onSuccessRedirectResponseWithJWTCookie(redirectPath, response, oauthJsonWebTokenDto);
        }
        else {
            throw new CommonException(ErrorCode.INVALID_PRINCIPAL_TYPE);
        }
    }
}
