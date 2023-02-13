package com.cm.common.model.domain;

import com.cm.common.util.AuthorizationUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Id
    @Column(name = "id", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @ManyToOne(targetEntity = AppUserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "email")
    private AppUserEntity updatedBy;
    @ManyToOne(targetEntity = AppUserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private AppUserEntity createdBy;


    @PreUpdate
    void preUpdate() {
        if (Objects.isNull(updatedDate)) {
            updatedDate = LocalDateTime.now();
        }
        if (Objects.isNull(updatedBy)) {
            updatedBy = AuthorizationUtil.getCurrentUserNullable().getAppUserEntity();
        }
    }


}
