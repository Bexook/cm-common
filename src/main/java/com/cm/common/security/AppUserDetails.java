package com.cm.common.security;

import com.cm.common.model.domain.AppUserEntity;
import com.cm.common.model.enumeration.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class AppUserDetails implements UserDetails {

    @JsonIgnore
    private final AppUserEntity appUserEntity;

    public AppUserDetails(final AppUserEntity appUserEntity) {
        this.appUserEntity = appUserEntity;
    }


    public Long getUserId() {
        return appUserEntity.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(appUserEntity.getUserRole().toString()));
    }

    @Override
    public String getPassword() {
        return appUserEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return appUserEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return appUserEntity.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return appUserEntity.isActive() && appUserEntity.isEmailVerified();
    }

    public UserRole getUserRole() {
        return appUserEntity.getUserRole();
    }

    public AppUserEntity getAppUserEntity() {
        return this.appUserEntity;
    }

    public boolean nonNullProperties() {
        return Objects.nonNull(this.appUserEntity) &&
                Objects.nonNull(this.appUserEntity.getPassword()) &&
                Objects.nonNull(this.appUserEntity.getEmail()) &&
                Objects.nonNull(this.appUserEntity.getUserRole());
    }

}
