package com.petrpopov.cheatfood.model.entity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * User: petrpopov
 * Date: 28.07.13
 * Time: 12:02
 */
public class Rate implements Serializable {

    @NotNull
    private String userId;

    @NotNull
    private Integer value;

    private Date date;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
