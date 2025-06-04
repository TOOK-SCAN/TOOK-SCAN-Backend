package com.tookscan.tookscan.security.handler.logout;

import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.utility.CookieUtil;
import com.tookscan.tookscan.security.application.usecase.LogoutUseCase;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultLogoutProcessHandler implements LogoutHandler {

    private final LogoutUseCase logoutUseCase;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        if (authentication == null) {
            return;
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        logoutUseCase.execute(principal);
    }
}
