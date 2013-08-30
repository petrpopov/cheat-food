package com.petrpopov.cheatfood.web.other;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 30.08.13
 * Time: 12:46
 */
public class EmailCallback {

    private String oauth_verifier;

    @NotNull
    @NotEmpty
    @Email
    private String emailConnect;
    private String code;

    public EmailCallback() {
    }

    public EmailCallback(String oauth_verifier, String emailConnect, String code) {
        this.oauth_verifier = oauth_verifier;
        this.emailConnect = emailConnect;
        this.code = code;
    }

    public String getOauth_verifier() {
        return oauth_verifier;
    }

    public void setOauth_verifier(String oauth_verifier) {
        this.oauth_verifier = oauth_verifier;
    }

    public String getEmailConnect() {
        return emailConnect;
    }

    public void setEmailConnect(String emailConnect) {
        this.emailConnect = emailConnect;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
