package com.tookscan.tookscan.security.filter;

import com.tookscan.tookscan.core.utility.StructuredLoggerUtil.MDCUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

public class MdcUserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String userId = userDetails.getUsername(); // 혹은 커스텀 필드의 UUID
                String userRole = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .orElse("ROLE_UNKNOWN");

                MDCUtil.setUserContext(userId, userRole);
            }
        }

        filterChain.doFilter(request, response);
    }
}
