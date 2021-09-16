package com.sts.finncub.usermanagement.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends Exception {

    private static final long serialVersionUID = -282913843145658333L;

    private int code;

    private HttpStatus httpStatus;

    private Object responseObject;

    public UnauthorizedException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public UnauthorizedException(String exceptionMessage, HttpStatus httpStatus) {
        super(exceptionMessage);
        this.code = httpStatus.value();
        this.httpStatus = httpStatus;
    }

    public UnauthorizedException(String exceptionMessage, Object responseObject, HttpStatus httpStatus) {
        super(exceptionMessage);
        this.code = httpStatus.value();
        this.httpStatus = httpStatus;
        this.responseObject = responseObject;
    }


}
