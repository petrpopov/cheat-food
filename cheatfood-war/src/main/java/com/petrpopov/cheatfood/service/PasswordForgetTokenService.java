package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.entity.PasswordForgetToken;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * User: petrpopov
 * Date: 12.08.13
 * Time: 18:55
 */

@Component
public class PasswordForgetTokenService extends TokenService<PasswordForgetToken> {

    @Autowired
    private UserService userService;

    public PasswordForgetTokenService() {
        super(PasswordForgetToken.class);
        logger = Logger.getLogger(PasswordForgetTokenService.class);
    }


    @Override
    protected void additionalChecks(String email, String userId) throws CheatException {

        UserEntity userByEmail = userService.getUserByEmail(email);
        if( userByEmail == null ) {
            throw new CheatException(ErrorType.no_user_with_such_email);
        }
    }

    @Override
    protected PasswordForgetToken generateTokenObject(String email, String tokenValue, String userId) throws CheatException {

        PasswordForgetToken token = new PasswordForgetToken();

        token.setEmail(email);
        token.setValue(tokenValue);
        token.setValid(true);
        token.setCreationDate(new Date());

        return token;
    }
}
