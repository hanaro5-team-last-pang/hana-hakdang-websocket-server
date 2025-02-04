package com.hanahakdangwebsocketserver.config;

import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Log4j2
@Component
@RequiredArgsConstructor
public class TokenHandler {

  @Value("${spring.jwt.secret}")
  private String secretKey;

  private static final String TOKEN_PREFIX = "Bearer ";

  public Long getUserId(HttpServletRequest request) {
    String token = resolveTokenFromRequest(request);
    if (validateToken(token)) {
      return getUserIdFromToken(token).orElse(null);
    }
    return null;
  }

  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }

    try {
      Claims claims = this.parseClaims(token);
      boolean isValid = !claims.getExpiration().before(new Date());
      log.info("유효한 토큰 여부: {}", isValid);
      return isValid;
    } catch (ExpiredJwtException e) {
      log.warn("만료된 토큰: {}", token);
      return false;
    } catch (Exception e) {
      log.warn("유효하지 않은 토큰: {}", token);
      return false;
    }
  }

  private Optional<Long> getUserIdFromToken(String token) {
    try {
      Object userId = this.parseClaims(token).get("id");
      if (userId instanceof Number) {
        return Optional.of(((Number) userId).longValue());
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Claims parseClaims(String token) {
    SecretKey encryptedKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

    try {
      return Jwts.parserBuilder().
          setSigningKey(encryptedKey).build().parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      log.warn("JWT가 만료되었습니다: {}", e.getMessage());
      return e.getClaims();
    } catch (Exception e) {
      log.error("JWT 파싱 중 오류 발생: {}", e.getMessage());
      throw e; // validateToken 호출하는 쪽에서 이 예외를 캐치하도록 함
    }
  }

  // request 헤더에서 token 가져오기
  public String resolveTokenFromRequest(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith(TOKEN_PREFIX)) {
      return token.substring(7);
    } else {
      return null;
    }
  }
}
