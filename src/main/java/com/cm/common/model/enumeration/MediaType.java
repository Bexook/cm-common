package com.cm.common.model.enumeration;

import com.cm.common.exception.SystemException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

public enum MediaType {

    VIDEO(1), PDF_HOMEWORK(2), PDF_LESSON_MATERIAL(3), PRESENTATION(4), IMG(5);

    private Integer code;

    MediaType(final Integer code) {
        this.code = code;
    }

    public static MediaType getByCode(final Integer code) {
        return Arrays.stream(MediaType.values())
                .filter(v -> Objects.equals(code, v.getCode()))
                .findFirst()
                .orElseThrow(() -> new SystemException("Unsupported media type", HttpStatus.INTERNAL_SERVER_ERROR));
    }


    public Integer getCode() {
        return code;
    }
}
