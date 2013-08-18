package com.petrpopov.cheatfood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 10:41
 */

@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    private String foursquareId;
    private String foursquareToken;
    private String foursquareTwitterUsername;

    private String facebookId;
    private String facebookToken;

    private String twitterId;
    private String twitterToken;
    private String twitterUsername;

    private String firstName;
    private String lastName;
    private String cookieId;
    private String email;
    private String passwordHash;
    private String salt;

    private List<UserRole> roles;

    public UserEntity() {
    }

    public UserEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoursquareId() {
        return foursquareId;
    }

    public void setFoursquareId(String foursquareId) {
        this.foursquareId = foursquareId;
    }

    public String getFoursquareToken() {
        return foursquareToken;
    }

    public void setFoursquareToken(String foursquareToken) {
        this.foursquareToken = foursquareToken;
    }

    public String getFoursquareTwitterUsername() {
        return foursquareTwitterUsername;
    }

    public void setFoursquareTwitterUsername(String foursquareTwitterUsername) {
        this.foursquareTwitterUsername = foursquareTwitterUsername;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public String getTwitterToken() {
        return twitterToken;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }
}
