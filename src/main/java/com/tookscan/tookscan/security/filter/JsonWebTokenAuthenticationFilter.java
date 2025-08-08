package com.tookscan.tookscan.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.exception.type.HttpSecurityException;
import com.tookscan.tookscan.core.utility.CookieUtil;
import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.usecase.AuthenticateJsonWebTokenUseCase;
import com.tookscan.tookscan.security.application.usecase.ReadAccountBriefUseCase;
import com.tookscan.tookscan.security.application.usecase.ReissueJsonWebTokenUseCase;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import com.tookscan.tookscan.security.presentation.dto.response.ReadAccountBriefResponseDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JsonWebTokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticateJsonWebTokenUseCase authenticateJsonWebTokenUseCase;
    private final ReadAccountBriefUseCase readAccountBriefUseCase;
    private final ReissueJsonWebTokenUseCase reissueJsonWebTokenUseCase;

    private final JsonWebTokenUtil jsonWebTokenUtil;

    private final String cookieDomain;
    private final String accessTokenCookieName;
    private final String refreshTokenCookieName;
    private final String temporaryTokenCookieName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    static final String AUTH_BRIEFS_URL = "/v1/auth/briefs";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        Optional<String> accessTokenOptional = CookieUtil.refineCookie(request, accessTokenCookieName);

        if (AUTH_BRIEFS_URL.equals(requestURI)) {
            if (accessTokenOptional.isEmpty()) {
                writeGuestResponse(response);
                return;
            }

            try {
                Claims claims = jsonWebTokenUtil.validateToken(accessTokenOptional.get());
                UUID accountId = UUID.fromString(claims.get(Constants.ACCOUNT_ID_CLAIM_NAME, String.class));
                ReadAccountBriefResponseDto responseDto = readAccountBriefUseCase.execute(accountId);
                writeAccountBriefResponse(response, responseDto);
                return;
            } catch (HttpSecurityException e) {
                if (e.getErrorCode() == ErrorCode.EXPIRED_TOKEN_ERROR) {
                    // 리프레시 토큰을 가져옵니다.
                    Optional<String> refreshTokenOptional = CookieUtil.refineCookie(request,
                            refreshTokenCookieName);
                    if (refreshTokenOptional.isEmpty()) {
                        clearTokenCookies(request, response);
                        throw new HttpSecurityException(
                                "리프레시 토큰이 없습니다. 다시 로그인해주세요.",
                                ErrorCode.INVALID_TOKEN_ERROR
                        );
                    }

                    // 리프레시 토큰으로 새 토큰들을 발급받습니다.
                    String refreshToken = refreshTokenOptional.get();
                    var newTokens = reissueJsonWebTokenUseCase.execute(refreshToken);

                    // 새로운 토큰으로 쿠키를 업데이트합니다.
                    CookieUtil.addCookie(response, cookieDomain, accessTokenCookieName,
                            newTokens.getAccessToken());
                    CookieUtil.addSecureCookie(response, cookieDomain, refreshTokenCookieName,
                            newTokens.getRefreshToken(),
                            (int) (jsonWebTokenUtil.getRefreshTokenExpirePeriod() / 1000L));

                    // 재발급된 새 액세스 토큰으로 다시 계정 정보 조회를 시도합니다.
                    Claims claims = jsonWebTokenUtil.validateToken(newTokens.getAccessToken());
                    UUID accountId = UUID.fromString(claims.get(Constants.ACCOUNT_ID_CLAIM_NAME, String.class));
                    ReadAccountBriefResponseDto responseDto = readAccountBriefUseCase.execute(accountId);
                    writeAccountBriefResponse(response, responseDto);
                    return;
                }
                clearTokenCookies(request, response);
                throw e;
            } catch (Exception e) {
                throw e;
            }
        }

        String accessToken = accessTokenOptional.orElseThrow(() -> new CommonException(ErrorCode.TOKEN_TYPE_ERROR));

        try {
            // 액세스 토큰 검증
            Claims claims = jsonWebTokenUtil.validateToken(accessToken);

            UUID accountId = UUID.fromString(claims.get(Constants.ACCOUNT_ID_CLAIM_NAME, String.class));
            ESecurityRole role = ESecurityRole.fromString(claims.get(Constants.ACCOUNT_ROLE_CLAIM_NAME, String.class));

            CustomUserPrincipal principal = authenticateJsonWebTokenUseCase.execute(accountId);

            if (!role.equals(principal.getRole())) {
                throw new CommonException(ErrorCode.ACCESS_DENIED);
            }

            // AuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );

            // SecurityContext에 AuthenticationToken 저장
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);

            // 다음 필터로 전달
            filterChain.doFilter(request, response);

        } catch (HttpSecurityException e) {
            if (e.getErrorCode() == ErrorCode.EXPIRED_TOKEN_ERROR) {
                // 액세스 토큰 만료 시 자동 재발급 시도
                if (tryRefreshToken(request, response, filterChain)) {
                    return; // 재발급 성공 시 요청 계속 처리
                }
            }
            clearTokenCookies(request, response);
            throw e;
        }
    }

    /**
     * 헤더가 없을 경우, 게스트 응답(JSON)을 작성하여 반환
     */
    private void writeGuestResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> guestData = new HashMap<>();
        guestData.put("account_type", ESecurityRole.GUEST);
        guestData.put("name", null);
        guestData.put("provider", null);

        Map<String, Object> guestResponse = new HashMap<>();
        guestResponse.put("success", true);
        guestResponse.put("data", guestData);
        guestResponse.put("error", null);

        objectMapper.writeValue(response.getWriter(), guestResponse);
    }

    /**
     * 헤더가 있는 경우, 계정 정보 간단 조회 응답을 반환
     */
    private void writeAccountBriefResponse(HttpServletResponse response, ReadAccountBriefResponseDto responseDto) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("account_type", responseDto.getAccountType());
        responseData.put("name", responseDto.getName());
        responseData.put("provider", responseDto.getProvider());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        responseMap.put("data", responseData);
        responseMap.put("error", null);

        objectMapper.writeValue(response.getWriter(), responseMap);
    }

    /**
     * 리프레시 토큰을 이용한 액세스 토큰 재발급 시도
     */
    private boolean tryRefreshToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Optional<String> refreshTokenOptional = CookieUtil.refineCookie(request, refreshTokenCookieName);
            if (refreshTokenOptional.isEmpty()) {
                return false; // 리프레시 토큰이 없으면 재발급 불가
            }

            // 리프레시 토큰으로 새 토큰 발급
            String refreshToken = refreshTokenOptional.get();
            var newTokens = reissueJsonWebTokenUseCase.execute(refreshToken);

            // 새로운 액세스 토큰으로 쿠키 설정
            CookieUtil.addCookie(response, cookieDomain, accessTokenCookieName, newTokens.getAccessToken());
            CookieUtil.addSecureCookie(response, cookieDomain, refreshTokenCookieName, newTokens.getRefreshToken(),
                    (int) (jsonWebTokenUtil.getRefreshTokenExpirePeriod() / 1000L));

            // 새 액세스 토큰으로 인증 처리
            Claims claims = jsonWebTokenUtil.validateToken(newTokens.getAccessToken());
            UUID accountId = UUID.fromString(claims.get(Constants.ACCOUNT_ID_CLAIM_NAME, String.class));
            ESecurityRole role = ESecurityRole.fromString(claims.get(Constants.ACCOUNT_ROLE_CLAIM_NAME, String.class));

            CustomUserPrincipal principal = authenticateJsonWebTokenUseCase.execute(accountId);

            if (!role.equals(principal.getRole())) {
                return false;
            }

            // AuthenticationToken 생성 및 SecurityContext 설정
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);

            // 다음 필터로 전달
            filterChain.doFilter(request, response);
            return true;

        } catch (Exception e) {
            // 리프레시 토큰도 만료되었거나 유효하지 않음
            return false;
        }
    }

    /**
     * 토큰 관련 쿠키 삭제
     */
    private void clearTokenCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, cookieDomain, accessTokenCookieName);
        CookieUtil.deleteCookie(request, response, cookieDomain, refreshTokenCookieName);
        CookieUtil.deleteCookie(request, response, cookieDomain, temporaryTokenCookieName);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 URL 목록에 포함되는지 확인
        return Constants.NO_NEED_AUTH_URLS.stream()
                .anyMatch(excludePattern -> requestURI.matches(excludePattern.replace("**", ".*")));
    }
}

