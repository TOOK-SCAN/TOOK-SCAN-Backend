package com.tookscan.tookscan.security.filter;

import com.tookscan.tookscan.core.constant.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class RedirectUrlFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith(Constants.KAKAO_OAUTH2_HREF) || requestURI.startsWith(Constants.GOOGLE_OAUTH2_HREF) || requestURI.startsWith(Constants.NAVER_OAUTH2_HREF)) {
            String redirectPath = request.getParameter("redirect-path");
            if (redirectPath != null && !redirectPath.isEmpty()) {
                request.getSession().setAttribute("REDIRECT_PATH", redirectPath);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !(requestURI.startsWith(Constants.KAKAO_OAUTH2_HREF) || requestURI.startsWith(Constants.GOOGLE_OAUTH2_HREF) || requestURI.startsWith(Constants.NAVER_OAUTH2_HREF));
    }
}
