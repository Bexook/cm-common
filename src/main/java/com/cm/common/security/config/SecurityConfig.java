package com.cm.common.security.config;

import com.cm.common.security.filter.JwtValidationRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtValidationRequestFilter jwtValidationRequestFilter;
    @Value("#{'${security.permit-all}'.split(',')}")
    private final Set<String> permitAllUrls;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity httpSecurity) throws Exception {
        String[] permitAll = permitAllUrls.toArray(new String[0]);
        httpSecurity
                .csrf().disable()
                .authorizeRequests().antMatchers(permitAllUrls.toArray(permitAll))
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterAfter(jwtValidationRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();

    }

}
