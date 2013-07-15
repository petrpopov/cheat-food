package com.petrpopov.cheatfood.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 12.07.13
 * Time: 2:12
 */
public class Text {

    @NotNull
    @NotEmpty
    private String language;

    @NotNull
    @NotEmpty
    private String value;

    public Text() {
    }

    public Text(String language, String value) {
        this.language = language;
        this.value = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Text text = (Text) o;

        if (language != null ? !language.equals(text.language) : text.language != null) return false;
        if (value != null ? !value.equals(text.value) : text.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = language != null ? language.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
