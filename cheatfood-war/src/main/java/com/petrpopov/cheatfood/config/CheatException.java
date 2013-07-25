package com.petrpopov.cheatfood.config;

import com.petrpopov.cheatfood.model.ErrorType;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 18:24
 */
public class CheatException extends Exception {

    private ErrorType errorType;

    public CheatException(String s) {
        super(s);
    }

    public CheatException(Throwable throwable) {
        super(throwable);
    }

    public CheatException(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
