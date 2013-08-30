package com.petrpopov.cheatfood.connection;

/**
 * User: petrpopov
 * Date: 30.08.13
 * Time: 12:03
 */
public class UserEmailInfo {

    private String userId;
    private Boolean newUser = Boolean.FALSE;
    private String email;

    public UserEmailInfo() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getNewUser() {
        return newUser;
    }

    public void setNewUser(Boolean newUser) {
        this.newUser = newUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
