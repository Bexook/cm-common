package com.cm.common.exception.handler;

import com.cm.common.exception.SystemException;
import com.cm.common.model.dto.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SystemExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {SystemException.class})
    public ResponseEntity<ErrorResponseDTO> handleSystemException(final SystemException ex) {
        final ErrorResponseDTO errorResponse = new ErrorResponseDTO()
                .setMessage(ex.getMessage())
                .setHttpStatus(ex.getServiceCode());
        return new ResponseEntity<>(errorResponse, ex.getServiceCode());
    }
}
