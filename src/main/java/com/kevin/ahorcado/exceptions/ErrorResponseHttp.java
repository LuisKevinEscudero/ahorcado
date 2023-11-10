package com.kevin.ahorcado.exceptions;

import java.io.Serializable;

public class ErrorResponseHttp implements Serializable {
    private static final long serialVersionUID = 1L;

    private String error;

    public ErrorResponseHttp() {
    }

    public ErrorResponseHttp(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorResponseHttp{" +
                "error='" + error + '\'' +
                '}';
    }
}
