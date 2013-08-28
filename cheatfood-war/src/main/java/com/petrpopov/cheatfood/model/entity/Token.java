package com.petrpopov.cheatfood.model.entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * User: petrpopov
 * Date: 28.08.13
 * Time: 16:08
 */
public class Token implements Serializable {

    @NotNull
    @NotEmpty
    @Indexed
    protected String value;

    protected Boolean valid;

    protected Date creationDate;

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
}
