package com.tookscan.tookscan.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.HeaderUtil;
import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.dto.response.ReadAccountBriefResponseDto;
import com.tookscan.tookscan.security.application.usecase.AuthenticateJsonWebTokenUseCase;
import com.tookscan.tookscan.security.application.usecase.ReadAccountBriefUseCase;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JsonWebTokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticateJsonWebTokenUseCase authenticateJsonWebTokenUseCase;
    private final ReadAccountBriefUseCase readAccountBriefUseCase;

    private final JsonWebTokenUtil jsonWebTokenUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    static final String AUTH_BRIEFS_URL = "/v1/auth/briefs";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        Optional<String> tokenOptional = HeaderUtil.refineHeader(request, Constants.AUTHORIZATION_HEADER, Constants.BEARER_PREFIX);

        if (AUTH_BRIEFS_URL.equals(requestURI)) {
            if (tokenOptional.isEmpty()) {
                writeGuestResponse(response);
                return;
            }

            try {
                Claims claims = jsonWebTokenUtil.validateToken(tokenOptional.get());
                UUID accountId = UUID.fromString(claims.get(Constants.ACCOUNT_ID_CLAIM_NAME, String.class));
                ReadAccountBriefResponseDto responseDto = readAccountBriefUseCase.execute(accountId);
                writeAccountBriefResponse(response, responseDto);
                return;
            } catch (Exception e) {
                writeGuestResponse(response);
                return;
            }
        }

        String token = tokenOptional.orElseThrow(() -> new CommonException(ErrorCode.INVALID_HEADER_ERROR));

        Claims claims = jsonWebTokenUtil.validateToken(token);

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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 URL 목록에 포함되는지 확인
        return Constants.NO_NEED_AUTH_URLS.stream()
                .anyMatch(excludePattern -> requestURI.matches(excludePattern.replace("**", ".*")));
    }
}

