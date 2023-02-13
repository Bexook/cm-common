package com.cm.common.model.enumeration;

import com.cm.common.exception.SystemException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

public enum CourseProgressStatus {

    ASSIGNED(0),
    IN_PROGRESS(1),
    EVALUATION_EXPECTED(2),
    CERTIFIED(3),
    FAILED(4),
    AWAITING_RETEST(5);

    Integer code;

    CourseProgressStatus(final Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static CourseProgressStatus getByCode(final Integer code) {
        return Arrays.stream(values())
                .filter(s -> Objects.equals(s.getCode(), code))
                .findFirst()
                .orElseThrow(() -> new SystemException("Invalid code value", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
