package com.cm.common.model.domain;

import com.cm.common.adapter.TokenTypeAdapter;
import com.cm.common.model.enumeration.TokenType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "active_token", schema = "system")
public class ActiveTokenEntity {

    @Id
    @Column(name = "id", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token")
    private String token;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "token_type")
    @Convert(converter = TokenTypeAdapter.class)
    private TokenType tokenType;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUserEntity user;

    public ActiveTokenEntity(final String token, final TokenType tokenType, final AppUserEntity user) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
    }

    public ActiveTokenEntity(final String token, final TokenType tokenType, final LocalDateTime createdDate, final AppUserEntity user) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.createdDate = createdDate;
    }

    @PrePersist
    void prePersist() {
        createdDate = LocalDateTime.now();
    }
}
