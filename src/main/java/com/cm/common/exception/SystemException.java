package com.cm.common.exception;

import org.springframework.http.HttpStatus;

public class SystemException extends RuntimeException {

    private HttpStatus serviceCode;

    public SystemException(final HttpStatus serviceCode) {
        this.serviceCode = serviceCode;
    }

    public SystemException(final String message, final HttpStatus serviceCode) {
        super(message);
        this.serviceCode = serviceCode;
    }

    public SystemException(final String message, final Throwable cause, final HttpStatus serviceCode) {
        super(message, cause);
        this.serviceCode = serviceCode;
    }

    public SystemException(final Throwable cause, final HttpStatus serviceCode) {
        super(cause);
        this.serviceCode = serviceCode;
    }

    public SystemException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace, final HttpStatus serviceCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.serviceCode = serviceCode;
    }


    public HttpStatus getServiceCode() {
        return serviceCode;
    }
}
