package com.petrpopov.cheatfood.web;

/**
 * User: petrpopov
 * Date: 17.07.13
 * Time: 16:05
 */
public class SocialAccessError {

    private String error;
    private String error_code;
    private String error_description;
    private String error_reason;

    public SocialAccessError() {
    }

    public SocialAccessError(String error, String error_code, String error_description, String error_reason) {
        this.error = error;
        this.error_code = error_code;
        this.error_description = error_description;
        this.error_reason = error_reason;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getError_reason() {
        return error_reason;
    }

    public void setError_reason(String error_reason) {
        this.error_reason = error_reason;
    }
}
