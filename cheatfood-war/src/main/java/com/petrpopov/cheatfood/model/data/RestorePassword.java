package com.petrpopov.cheatfood.model.data;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 12.08.13
 * Time: 22:59
 */
public class RestorePassword {

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String passwordCopy;

    @NotNull
    @NotEmpty
    private String token;

    public RestorePassword() {
    }

    public RestorePassword(String password, String passwordCopy, String token) {
        this.password = password;
        this.passwordCopy = passwordCopy;
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordCopy() {
        return passwordCopy;
    }

    public void setPasswordCopy(String passwordCopy) {
        this.passwordCopy = passwordCopy;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
