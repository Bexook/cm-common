package com.cm.common.repository;

import com.cm.common.model.domain.ActiveTokenEntity;
import com.cm.common.model.enumeration.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveTokenRepository extends JpaRepository<ActiveTokenEntity, Long> {

    ActiveTokenEntity findByTokenAndTokenType(final String token, final TokenType tokenType);

    @Query(value = "SELECT * FROM system.active_token token WHERE token.token_type = :#{#tokenType.getCode()} AND token.user_id = :userId", nativeQuery = true)
    ActiveTokenEntity findByUserAndTokenType(@Param("userId") final Long userId, @Param("tokenType") final TokenType tokenType);

    void deleteByTokenAndTokenType(final String token, final TokenType tokenType);


}
