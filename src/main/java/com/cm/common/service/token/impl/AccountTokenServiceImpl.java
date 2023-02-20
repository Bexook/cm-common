package com.cm.common.service.token.impl;

import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.ActiveTokenEntity;
import com.cm.common.model.domain.AppUserEntity;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.TokenType;
import com.cm.common.repository.ActiveTokenRepository;
import com.cm.common.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public abstract class AccountTokenServiceImpl implements TokenService {

    @Value("#{T(Long).parseLong('${token.activation.time-to-live}')}")
    private Long tokenTtlDays;
    @Autowired
    protected ActiveTokenRepository activeTokenRepository;

    @Autowired
    protected OrikaBeanMapper mapper;

    @PreAuthorize("@userAccessValidation.scheduledJob() || @userAccessValidation.isAdmin()")
    public void deleteAllTokensByUserId(final List<Long> userIds) {
        activeTokenRepository.deleteAllByUserId(userIds);
    }

    public AppUserDTO getUserByToken(final String token, final TokenType tokenType) {
        return mapper.map(activeTokenRepository.findByTokenAndTokenType(token, tokenType).getUser(), AppUserDTO.class);
    }

    public String getTokenByUserId(final Long userId, final TokenType tokenType) {
        return activeTokenRepository.findByUserAndTokenType(userId, tokenType).getToken();
    }

    public void deleteToken(final String token, final TokenType tokenType) {
        activeTokenRepository.deleteByTokenAndTokenType(token, tokenType);
    }

    public boolean isTokenValid(final String token, final TokenType tokenType) {
        final ActiveTokenEntity entity = activeTokenRepository.findByTokenAndTokenType(token, tokenType);
        final boolean valid = entity.getCreatedDate().plusDays(tokenTtlDays).isAfter(LocalDateTime.now());
        if (valid) {
            activeTokenRepository.deleteByTokenAndTokenType(token, tokenType);
        }
        return valid;
    }

    public String generateToken(final AppUserDTO appUserDetails, final TokenType tokenType) {
        final String activationToken = UUID.randomUUID().toString();
        activeTokenRepository.save(new ActiveTokenEntity(activationToken, tokenType, mapper.map(appUserDetails, AppUserEntity.class)));
        return activationToken;
    }


}
