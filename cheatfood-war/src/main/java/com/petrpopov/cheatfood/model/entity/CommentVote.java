package com.petrpopov.cheatfood.model.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: petrpopov
 * Date: 06.09.13
 * Time: 20:10
 */

@Document
public class CommentVote {

    @DBRef
    private UserEntity author;

    private String authorPublicName;

    private Boolean value;

    public CommentVote() {
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public String getAuthorPublicName() {
        return authorPublicName;
    }

    public void setAuthorPublicName(String authorPublicName) {
        this.authorPublicName = authorPublicName;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
