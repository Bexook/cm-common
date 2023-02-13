package com.cm.common.exception.handler;

import com.cm.common.exception.SystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.cm.common.*")
public class SystemExceptionControllerAdvice {

    @ExceptionHandler(value = {SystemException.class})
    public ResponseEntity<?> resourceNotFoundException(final SystemException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getServiceCode());
    }
}
