package com.cm.common.security.filter;


import com.cm.common.model.enumeration.TokenType;
import com.cm.common.security.AppUserDetails;
import com.cm.common.security.management.jwt.JwtService;
import com.cm.common.service.token.TokenService;
import com.cm.common.service.token.impl.TokenServiceImpl;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Objects;
import java.util.Set;

@Component
public class JwtValidationRequestFilter extends OncePerRequestFilter {

    private final Set<String> permitAllUrls;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserDetailsService appUserDetailsService;


    @Autowired
    public JwtValidationRequestFilter(@Value("#{'${security.permit-all}'.split(',')}") final Set<String> permitAllUrls,
                                      final JwtService jwtService,
                                      final TokenService tokenService,
                                      final UserDetailsService appUserDetailsService) {
        this.permitAllUrls = permitAllUrls;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.appUserDetailsService = appUserDetailsService;
    }

    @Override
    @SneakyThrows
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected void doFilterInternal(final HttpServletRequest request, final @NonNull HttpServletResponse response, final @NonNull FilterChain filterChain) {
        final URI requestUri = URI.create(request.getRequestURI());
        if (isPermitAllPath(requestUri)) {
            filterChain.doFilter(request, response);
        } else {
            final String token = jwtService.getTokenJwtFromRequest(request);
            final AppUserDetails appUserDetails = (AppUserDetails) appUserDetailsService.loadUserByUsername(jwtService.getPrincipalFromJwtToken(token));
            if (Objects.nonNull(appUserDetails) && appUserDetails.isEnabled() && tokenService.isJwtTokenValid(token, TokenType.JWT_TOKEN)) {
                final UsernamePasswordAuthenticationToken currentUser =
                        new UsernamePasswordAuthenticationToken(appUserDetails, null, null);
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(currentUser);
                filterChain.doFilter(request, response);
                return;
            }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private boolean isPermitAllPath(final URI uri) {
        return permitAllUrls.stream().map(url -> uri.getPath().startsWith(url.replace("*", ""))).reduce(Boolean.FALSE, Boolean::logicalOr);
    }

}
