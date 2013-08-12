package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.PasswordForgetToken;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User: petrpopov
 * Date: 12.08.13
 * Time: 18:55
 */

@Component
public class PasswordForgetTokenService extends GenericService<PasswordForgetToken> {

    private Logger logger = Logger.getLogger(PasswordForgetTokenService.class);

    public PasswordForgetTokenService() {
        super(PasswordForgetToken.class);
    }

    public List<PasswordForgetToken> findTokensForEmail(String email) {

        Criteria criteria = Criteria.where("email").is(email);
        Query query = new Query(criteria);

        List<PasswordForgetToken> list = op.find(query, PasswordForgetToken.class);
        return list;
    }

    public PasswordForgetToken createTokenForEmail(String email) {

        //paranoia style
        boolean ok = false;
        String token = null;
        while(!ok) {
            UUID uuid = UUID.randomUUID();
            token = uuid.toString().replaceAll("-", "");

            PasswordForgetToken byToken = this.findByToken(token);
            if( byToken == null ) {
                ok = true;
                break;
            }
        }

        PasswordForgetToken t = new PasswordForgetToken();
        t.setEmail(email);
        t.setValue(token);
        t.setValid(true);
        t.setCreationDate(new Date());

        op.save(t);
        return findByToken(token);
    }

    public PasswordForgetToken findByToken(String token) {
        Criteria criteria = Criteria.where("value").is(token);
        Query query = new Query(criteria);

        return op.findOne(query, PasswordForgetToken.class);
    }



    private boolean tokenListContains(List<PasswordForgetToken> list, String token) {

        for (PasswordForgetToken passwordForgetToken : list) {
            if( passwordForgetToken.getValue().equals(token)) {
                return true;
            }
        }

        return false;
    }
}
