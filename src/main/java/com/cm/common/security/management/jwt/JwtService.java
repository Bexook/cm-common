package com.cm.common.security.management.jwt;

import com.cm.common.model.dto.UserCredentialsDTO;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

public interface JwtService {

    void logoutJwt(final HttpServletRequest httpServletRequest) throws AuthenticationException;

    String loginJwt(final UserCredentialsDTO userCredentials) throws AuthenticationException;

    String getTokenJwtFromRequest(final HttpServletRequest httpServletRequest) throws AuthenticationException;


    String getPrincipalFromJwtToken(final String token);


}
