package com.petrpopov.cheatfood.model;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 24.07.13
 * Time: 22:08
 */
public class Vote {

    @NotNull
    private String userId;

    @NotNull
    private Boolean value;

    public Vote() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
