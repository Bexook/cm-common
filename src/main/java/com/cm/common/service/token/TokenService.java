package com.cm.common.service.token;

import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.TokenType;

import java.util.List;

public interface TokenService {

    boolean isTokenValid(final String token, final TokenType tokenType);

    String generateToken(final AppUserDTO appUserDetails, final TokenType tokenType);

    boolean isJwtTokenValid(final String token, final TokenType jwtToken);

    String getTokenByUserId(final Long userId, final TokenType tokenType);

    void deleteAllTokensByUserId(final List<Long> userIds);

    AppUserDTO getUserByToken(final String token, final TokenType tokenType);

    String generateJwtToken(final AppUserDTO appUser);

    void deleteToken(final String token, final TokenType tokenType);

}
