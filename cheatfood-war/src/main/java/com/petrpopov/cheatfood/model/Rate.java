package com.petrpopov.cheatfood.model;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 28.07.13
 * Time: 12:02
 */
public class Rate {

    @NotNull
    private String userId;

    @NotNull
    private Integer value;

    public Rate() {
    }

    public Rate(String userId, Integer value) {
        this.userId = userId;
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
