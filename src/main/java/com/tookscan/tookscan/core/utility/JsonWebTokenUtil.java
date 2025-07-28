package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.exception.type.HttpSecurityException;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.application.dto.OauthJsonWebTokenDto;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JsonWebTokenUtil implements InitializingBean {
    @Value("${json-web-token.secret-key}")
    private String secretKey;

    @Value("${json-web-token.temporary-token-expire-period}")
    private Long temporaryTokenExpirePeriod;

    @Value("${json-web-token.access-token-expire-period}")
    private Long accessTokenExpirePeriod;

    @Getter
    @Value("${json-web-token.refresh-token-expire-period}")
    private Long refreshTokenExpirePeriod;

    private SecretKey key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public DefaultJsonWebTokenDto generateDefaultJsonWebTokens(UUID id, ESecurityRole role) {
        return new DefaultJsonWebTokenDto(
                generateToken(id.toString(), role, accessTokenExpirePeriod),
                generateToken(id.toString(), null, refreshTokenExpirePeriod)
        );
    }

    public OauthJsonWebTokenDto generateOauthJsonWebTokens(String id) {
        return new OauthJsonWebTokenDto(
                generateToken(id, null, temporaryTokenExpirePeriod),
                null,
                null
        );
    }

    public OauthJsonWebTokenDto generateOauthJsonWebTokens(UUID id, ESecurityRole role) {
        return new OauthJsonWebTokenDto(
                null,
                generateToken(id.toString(), role, accessTokenExpirePeriod),
                generateToken(id.toString(), null, refreshTokenExpirePeriod)
        );
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException e) {
            throw new HttpSecurityException(e.getMessage(), ErrorCode.TOKEN_MALFORMED_ERROR);
        } catch (IllegalArgumentException e) {
            throw new HttpSecurityException(e.getMessage(), ErrorCode.TOKEN_TYPE_ERROR);
        } catch (ExpiredJwtException e) {
            throw new HttpSecurityException(e.getMessage(), ErrorCode.EXPIRED_TOKEN_ERROR);
        } catch (UnsupportedJwtException e) {
            throw new HttpSecurityException(e.getMessage(), ErrorCode.TOKEN_UNSUPPORTED_ERROR);
        } catch (JwtException e) {
            throw new HttpSecurityException(e.getMessage(), ErrorCode.TOKEN_UNKNOWN_ERROR);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 토큰 생성 메서드 (최신 API 적용)
     */
    private String generateToken(String identifier, ESecurityRole role, Long expirePeriod) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirePeriod);

        JwtBuilder builder = Jwts.builder()
                .subject(identifier)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key);

        if (role != null) {
            builder.claim(Constants.ACCOUNT_ROLE_CLAIM_NAME, role.name()); // claim 추가
        }

        return builder.compact();
    }
}
