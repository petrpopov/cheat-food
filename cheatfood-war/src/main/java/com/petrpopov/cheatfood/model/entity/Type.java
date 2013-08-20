package com.petrpopov.cheatfood.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: petrpopov
 * Date: 12.07.13
 * Time: 2:10
 */

@Document(collection = "types")
public class Type implements Serializable {

    @Id
    private String id;

    private String code;

    @NotNull
    @Valid
    private List<Text> names;

    public Type() {
    }

    public Type(Text name, String code) {
        this.code = code;
        this.names = new ArrayList<Text>();
        this.names.add(name);
    }

    public Type(String id, Text name) {
        this.id = id;
        this.names = new ArrayList<Text>();
        this.names.add(name);
    }

    public Type(String id, List<Text> names) {
        this.id = id;
        this.names = names;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Text> getNames() {
        return names;
    }

    public void setNames(List<Text> names) {
        this.names = names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Type type = (Type) o;

        if (id != null ? !id.equals(type.id) : type.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
