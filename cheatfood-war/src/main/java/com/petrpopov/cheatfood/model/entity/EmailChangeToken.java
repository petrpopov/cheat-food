package com.petrpopov.cheatfood.model.entity;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 28.08.13
 * Time: 15:43
 */

@Document(collection = "emailchangetokens")
public class EmailChangeToken extends Token {

    @Id
    private String id;

    @NotNull
    @NotEmpty
    @Email
    @Indexed
    private String email;

    @NotEmpty
    @NotNull
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
