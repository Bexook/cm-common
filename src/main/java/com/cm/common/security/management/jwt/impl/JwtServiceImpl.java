package com.cm.common.security.management.jwt.impl;

import com.cm.common.model.dto.UserCredentialsDTO;
import com.cm.common.model.enumeration.TokenType;
import com.cm.common.security.AppUserDetails;
import com.cm.common.security.management.jwt.JwtService;
import com.cm.common.service.token.impl.TokenServiceImpl;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.JwtUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${token.jwt.secret}")
    private String secretKey;
    private final String AUTHORIZATION = "Authorization";
    @Autowired
    private TokenServiceImpl tokenService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private UserDetailsService appUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void logoutJwt(final HttpServletRequest httpServletRequest) throws AuthenticationException {
        final String token = getTokenJwtFromRequest(httpServletRequest);
        tokenService.deleteToken(token, TokenType.JWT_TOKEN);
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
    }

    @Override
    public String loginJwt(final UserCredentialsDTO creds) throws AuthenticationException {
        final AppUserDetails userDetails = (AppUserDetails) appUserDetailsService.loadUserByUsername(creds.getLogin());
        if (Objects.nonNull(userDetails) &&
                Objects.nonNull(userDetails.getAppUserEntity()) &&
                userDetails.isEnabled() &&
                userDetails.nonNullProperties() &&
                passwordEncoder.matches(creds.getPassword(), userDetails.getPassword())) {
            return tokenService.generateJwtToken(appUserService.findByEmail(userDetails.getUsername()));
        }
        throw new AuthenticationException("Unknown user");
    }


    public String getTokenJwtFromRequest(final HttpServletRequest httpServletRequest) throws AuthenticationException {
        final String token = httpServletRequest.getHeader(AUTHORIZATION);
        if (Objects.nonNull(token) && Strings.isNotBlank(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        throw new AuthenticationException("Bearer token not found");
    }

    @Override
    public String getPrincipalFromJwtToken(final String token) {
        return JwtUtils.getClaims(token, secretKey).getSubject();
    }

}
