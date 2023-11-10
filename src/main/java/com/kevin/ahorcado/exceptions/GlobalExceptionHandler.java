package com.kevin.ahorcado.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ********************************************************************************************************
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleGenericException(Exception ex, WebRequest request){

        ErrorResponseHttp errorResponseHttp = new ErrorResponseHttp(ex.getMessage());

        return handleExceptionInternal(ex, errorResponseHttp, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    // ********************************************************************************************************

    // ********************************************************************************************************
    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<?> handleRuntimeExceptions(Exception ex, WebRequest request){

        ErrorResponseHttp errorResponseHttp = new ErrorResponseHttp(ex.getMessage());

        return handleExceptionInternal(ex, errorResponseHttp, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    // ********************************************************************************************************

    // ********************************************************************************************************
    @ExceptionHandler(PokemonException.class)
    protected ResponseEntity<?> handlePokemonExceptions(PokemonException ex, WebRequest request){

        ErrorResponseHttp errorResponseHttp = new ErrorResponseHttp(ex.getMessage());

        return handleExceptionInternal(ex, errorResponseHttp, new HttpHeaders(), ex.getHttpStatus(), request);
    }
    // ********************************************************************************************************

    @ExceptionHandler({RestClientException.class, HttpClientErrorException.class})
    protected ResponseEntity<?> handleRestClientException(Exception ex, WebRequest request) {
        ErrorResponseHttp errorResponseHttp = new ErrorResponseHttp(ex.getMessage());
        return handleExceptionInternal(ex, errorResponseHttp, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({JsonProcessingException.class, JsonMappingException.class})
    protected ResponseEntity<?> handleJsonExceptions(Exception ex, WebRequest request) {
        ErrorResponseHttp errorResponseHttp = new ErrorResponseHttp("Error en el procesamiento del JSON.");
        return handleExceptionInternal(ex, errorResponseHttp, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}

