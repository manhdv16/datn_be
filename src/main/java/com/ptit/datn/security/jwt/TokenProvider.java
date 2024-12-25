package com.ptit.datn.security.jwt;

import static com.ptit.datn.security.SecurityUtils.AUTHORITIES_KEY;

import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.management.SecurityMetersService;
import com.ptit.datn.service.RedisService;
import com.ptit.datn.utils.DataUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tech.jhipster.config.JHipsterProperties;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String INVALID_JWT_TOKEN = "Invalid JWT token.";

    private final Key key;

    private final JwtParser jwtParser;

    private final long tokenValidityInMilliseconds;

    private final SecurityMetersService securityMetersService;

    private final RedisService redisService;

    public TokenProvider(JHipsterProperties jHipsterProperties, SecurityMetersService securityMetersService, RedisService redisService) {
        byte[] keyBytes;
        String secret = jHipsterProperties.getSecurity().getAuthentication().getJwt().getBase64Secret();
        if (!ObjectUtils.isEmpty(secret)) {
            log.debug("Using a Base64-encoded JWT secret key");
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            log.warn(
                "Warning: the JWT key used is not Base64-encoded. " +
                "We recommend using the `jhipster.security.authentication.jwt.base64-secret` key for optimum security."
            );
            secret = jHipsterProperties.getSecurity().getAuthentication().getJwt().getSecret();
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        this.tokenValidityInMilliseconds = 1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.securityMetersService = securityMetersService;
        this.redisService = redisService;
    }

    public String createToken(Long id, Authentication authentication) {
        if(id == null) {
            throw new AppException(ErrorCode.USERID_NOT_FOUND);
        }

        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        return Jwts.builder()
            .setSubject(String.valueOf(id))
            .signWith(key, SignatureAlgorithm.HS512)
            .claim(AUTHORITIES_KEY, authorities)
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + this.tokenValidityInMilliseconds))
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        String strRole = claims.get(AUTHORITIES_KEY).toString();
        Stream<List<String>> roles = Stream.of(List.of(strRole.split(" ")));
        authorities = roles.flatMap(Collection::stream).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        String id = redisService.findByKey(authToken);
        if (DataUtils.isNullOrEmpty(id)) {
            throw new ExpiredJwtException(null, null, "Token is expired");
        }
        try {
            jwtParser.parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            this.securityMetersService.trackTokenExpired();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            this.securityMetersService.trackTokenUnsupported();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (MalformedJwtException e) {
            this.securityMetersService.trackTokenMalformed();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (SignatureException e) {
            this.securityMetersService.trackTokenInvalidSignature();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (IllegalArgumentException e) {
            log.error("Token validation error {}", e.getMessage());
        }

        return false;
    }

    public String parseToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

    }
}
