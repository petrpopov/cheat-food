package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.entity.EmailChangeToken;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * User: petrpopov
 * Date: 28.08.13
 * Time: 15:49
 */

@Component
public class EmailChangeTokenService extends TokenService<EmailChangeToken> {

    public EmailChangeTokenService() {
        super(EmailChangeToken.class);
        logger = Logger.getLogger(EmailChangeTokenService.class);
    }

    @Override
    protected void additionalChecks(String email, String userId) throws CheatException {

    }

    @Override
    protected EmailChangeToken generateTokenObject(String email, String tokenValue, String userId) throws CheatException {

        EmailChangeToken token = new EmailChangeToken();

        token.setEmail(email);
        token.setValue(tokenValue);
        token.setValid(true);
        token.setCreationDate(new Date());
        token.setUserId(userId);

        return token;
    }
}
