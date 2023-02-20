package com.cm.common.util;


import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationUtil {

    public static Object getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
