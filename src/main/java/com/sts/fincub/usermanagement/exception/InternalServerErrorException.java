package com.sts.fincub.usermanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerErrorException extends Exception {
    private static final long serialVersionUID = -282913843145658233L;

    private int code;

    private HttpStatus httpStatus;

    private Object responseObject;

    public InternalServerErrorException(String message){
        super(message);
    }

    public InternalServerErrorException(String message,HttpStatus status){
        super(message);
        this.code=status.value();
        this.httpStatus = status;
    }

    public InternalServerErrorException(String message,HttpStatus status,Object responseObject){
        super(message);
        this.code=status.value();
        this.httpStatus = status;
        this.responseObject = responseObject;
    }
}
