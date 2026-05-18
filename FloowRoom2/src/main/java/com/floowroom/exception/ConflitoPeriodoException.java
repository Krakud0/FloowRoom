package com.floowroom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflitoPeriodoException extends RuntimeException {
    public ConflitoPeriodoException(String msg) { super(msg); }
}
