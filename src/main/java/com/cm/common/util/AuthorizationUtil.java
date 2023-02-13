package com.cm.common.util;


import com.cm.common.security.AppUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationUtil {

    public static AppUserDetails getCurrentUserNullable() {
        final Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user instanceof String ? null : (AppUserDetails) user;
    }

}
