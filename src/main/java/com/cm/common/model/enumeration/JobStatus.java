package com.cm.common.model.enumeration;

import com.cm.common.exception.SystemException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

public enum JobStatus {

    FAILED(0),
    SUCCEEDED(1);

    private Integer code;

    JobStatus(final Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }


    public static JobStatus getByCode(final Integer code) {
        return Arrays.stream(values())
                .filter(v -> Objects.equals(v.getCode(), code))
                .findFirst()
                .orElseThrow(() -> new SystemException("Status does not exist by code", HttpStatus.BAD_REQUEST));
    }
}
