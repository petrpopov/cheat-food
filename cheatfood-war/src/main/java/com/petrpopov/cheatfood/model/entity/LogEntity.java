package com.petrpopov.cheatfood.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * User: petrpopov
 * Date: 27.08.13
 * Time: 21:07
 */

@Document(collection = "locationLogs")
public class LogEntity {

    @Id
    private String id;

    private Date dateTime;
    private UserEntity owner;
    private LogAction action;

    private Location before;
    private Location after;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public LogAction getAction() {
        return action;
    }

    public void setAction(LogAction action) {
        this.action = action;
    }

    public Location getBefore() {
        return before;
    }

    public void setBefore(Location before) {
        this.before = before;
    }

    public Location getAfter() {
        return after;
    }

    public void setAfter(Location after) {
        this.after = after;
    }
}
