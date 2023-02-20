package com.cm.common.service.token.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.model.domain.ActiveTokenEntity;
import com.cm.common.model.domain.AppUserEntity;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.TokenType;
import com.cm.common.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Service
public class TokenServiceImpl extends AccountTokenServiceImpl {

    @Value("${token.jwt.secret}")
    private String secretKey;
    @Value("${token.jwt.expiration}")
    private int expirationPeriod;

    @Override
    public String generateJwtToken(final AppUserDTO appUserDetails) {
        Date expiration = Date.from(Instant.from(LocalDate.now().plusDays(expirationPeriod).atStartOfDay(ZoneId.systemDefault())));
        Claims claims = Jwts.claims().setSubject(appUserDetails.getEmail());
        claims.put("userRole", appUserDetails.getUserRole());
        claims.setExpiration(expiration);
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        if (!activeTokenRepository.exists(Example.of(new ActiveTokenEntity().setToken(token), ExampleMatcher.matching()))) {
            activeTokenRepository.save(new ActiveTokenEntity(token, TokenType.JWT_TOKEN, mapper.map(appUserDetails, AppUserEntity.class)));
        }
        return token;
    }

    @Override
    public boolean isJwtTokenValid(final String token, final TokenType jwtToken) {
        if (jwtToken != TokenType.JWT_TOKEN) {
            throw new SystemException("Required JWT_TOKEN", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ActiveTokenEntity tokenFromDB = activeTokenRepository.findByTokenAndTokenType(token, jwtToken);
        return Objects.nonNull(tokenFromDB) && new Date(System.currentTimeMillis()).before(getJwtTokenExpiration(token));
    }

    private Date getJwtTokenExpiration(final String token) {
        return JwtUtils.getClaims(token, secretKey).getExpiration();
    }

}
