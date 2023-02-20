package com.cm.common.model.enumeration;

import com.cm.common.exception.SystemException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum UserRole {

    STUDENT(0),
    TEACHER(1),
    ADMIN(2),
    GOD_MODE(3),
    SCHEDULED_JOB(4);

    private Integer code;

    UserRole(Integer code) {
        this.code = code;
    }


    public static UserRole getByCode(final Integer code) {
        return Arrays.stream(UserRole.values()).filter(ur -> ur.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new SystemException("Unsupported value", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public Integer getCode() {
        return code;
    }
}
