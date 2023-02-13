package com.cm.common.controller;

import com.cm.common.model.dto.UserCredentialsDTO;
import com.cm.common.security.management.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class JwtAuthResource {
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Validated UserCredentialsDTO creds) throws AuthenticationException {
        return ResponseEntity.ok(jwtService.loginJwt(creds));
    }

    @PostMapping("/logout")
    public void logout(final HttpServletRequest httpServletRequest) throws AuthenticationException {
        jwtService.logoutJwt(httpServletRequest);
    }

}
