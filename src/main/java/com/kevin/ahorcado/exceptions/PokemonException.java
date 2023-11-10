package com.kevin.ahorcado.exceptions;

import org.springframework.http.HttpStatus;

public class PokemonException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;

    public PokemonException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public PokemonException(String message, int httpStatusCode) {
        super(message);
        this.httpStatus = HttpStatus.valueOf(httpStatusCode);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return "PokemonException[ httpStatus=" + httpStatus + "]";
    }
}

