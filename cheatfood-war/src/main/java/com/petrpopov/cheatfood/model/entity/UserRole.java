package com.petrpopov.cheatfood.model.entity;

import com.mongodb.BasicDBObject;

import java.io.Serializable;

/**
 * User: petrpopov
 * Date: 11.08.13
 * Time: 23:42
 */
public class UserRole implements Serializable {

    public static String ROLE_USER = "ROLE_USER";
    public static String ROLE_ADMIN = "ROLE_ADMIN";

    private String name;

    public UserRole() {
    }

    public UserRole(String name) {
        this.name = name;
    }

    public static UserRole getRoleUser() {
        return new UserRole(ROLE_USER);
    }

    public BasicDBObject getBasicDBObject() {

        BasicDBObject db = new BasicDBObject("name", name);
        return db;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRole userRole = (UserRole) o;

        if (name != null ? !name.equals(userRole.name) : userRole.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
