package com.cm.common.model.enumeration;

import org.springframework.security.core.GrantedAuthority;

public enum CourseAuthorities implements GrantedAuthority {

    READ_COURSE,
    UPDATE_COURSE,
    UPDATE_LESSONS,
    CREATE_LESSONS,
    CREATE_EXAMS,
    UPDATE_EXAMS;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
