package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.entity.Token;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * User: petrpopov
 * Date: 28.08.13
 * Time: 16:05
 */

@Component
public abstract class TokenService<T extends Token> extends GenericService<T> {

    public TokenService(Class<T> domainClass) {
        super(domainClass);
    }

    public T createTokenForEmail(String email, String userId) throws CheatException {

        logger.info("Creating token for email: " + email);

        additionalChecks(email, userId);

        String token = this.generateUniqueTokenValue();

        T t = generateTokenObject(email, token, userId);
        op.save(t);

        return findByTokenValue(token);
    }

    protected abstract void additionalChecks(String email, String userId) throws CheatException;
    protected abstract T generateTokenObject(String email, String tokenValue, String userId) throws CheatException;

    public List<T> findTokensForEmail(String email) {

        logger.info("Searching for tokens by email: " + email);

        Criteria criteria = Criteria.where("email").is(email);
        Query query = new Query(criteria);

        List<T> list = op.find(query, domainClass);
        return list;
    }

    public T findByTokenValue(String token) {
        Criteria criteria = Criteria.where("value").is(token);
        Query query = new Query(criteria);

        return op.findOne(query, domainClass);
    }

    public void invalidateTokensForEmail(String email) {

        logger.info("Invalidating tokens for email: " + email);

        List<T> list = findTokensForEmail(email);
        for (T token : list) {
            token.setValid(false);
            op.save(token);
        }
    }

    protected String generateUniqueTokenValue() {

        //paranoia style
        boolean ok = false;
        String token = null;
        while(!ok) {
            UUID uuid = UUID.randomUUID();
            token = uuid.toString().replaceAll("-", "");

            T byToken = this.findByTokenValue(token);
            if( byToken == null ) {
                ok = true;
                break;
            }
        }

        return token;
    }
}
