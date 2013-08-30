package com.petrpopov.cheatfood.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * User: petrpopov
 * Date: 30.08.13
 * Time: 18:12
 */

@Document(collection = "userconnections")
public class UserConnections {

    @Id
    private String id;

    @DBRef
    private UserEntity user;

    private List<String> locations;

    public UserConnections() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
