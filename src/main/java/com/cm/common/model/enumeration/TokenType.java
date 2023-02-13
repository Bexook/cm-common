package com.cm.common.model.enumeration;

import com.cm.common.exception.SystemException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

public enum TokenType {

    JWT_TOKEN(1),
    ACCOUNT_ACTIVATION_TOKEN(2),
    PASSWORD_RESET_TOKEN(3);

    private Integer code;

    TokenType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static TokenType getByCode(final Integer code) {
        return Arrays.stream(values())
                .filter(v -> Objects.equals(v.getCode(), code))
                .findFirst()
                .orElseThrow(() -> new SystemException("Non valid token type", HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
