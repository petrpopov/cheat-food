package com.petrpopov.cheatfood.model.entity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * User: petrpopov
 * Date: 24.07.13
 * Time: 22:08
 */
public class Vote implements Serializable {

    @NotNull
    private String userId;

    @NotNull
    private Boolean value;

    private Date date;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
