package com.petrpopov.cheatfood.security;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 16.07.13
 * Time: 16:48
 */

@Component("passwordEncoder")
public class CheatPasswordEncoder extends ShaPasswordEncoder {

    public CheatPasswordEncoder()
    {
        super(256);
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt)
    {
        String encodePassword = this.encodePassword(rawPass, salt);
        if( encPass.equals(encodePassword))
            return true;
        return false;
    }
}
