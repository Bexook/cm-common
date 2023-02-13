package com.cm.common.service.token;

import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.ActiveTokenEntity;
import com.cm.common.model.domain.AppUserEntity;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.TokenType;
import com.cm.common.repository.ActiveTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public abstract class TokenService {

    @Value("#{T(Long).parseLong('${token.activation.time-to-live}')}")
    private Long tokenTtlDays;
    @Autowired
    protected ActiveTokenRepository activeTokenRepository;

    @Autowired
    protected OrikaBeanMapper mapper;

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
