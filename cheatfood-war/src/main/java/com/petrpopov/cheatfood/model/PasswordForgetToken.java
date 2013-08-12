package com.petrpopov.cheatfood.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: petrpopov
 * Date: 12.08.13
 * Time: 18:53
 */

@Document(collection = "passwordforgettokens")
public class PasswordForgetToken {

    @Id
    private String id;

    @NotNull
    @NotEmpty
    @Indexed
    private String email;

    @NotNull
    @NotEmpty
    @Indexed
    private String value;

    private Boolean valid;

    private Date creationDate;

    public PasswordForgetToken() {
    }

    public PasswordForgetToken(String email, String value, Boolean valid) {
        this.email = email;
        this.value = value;
        this.valid = valid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordForgetToken that = (PasswordForgetToken) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
