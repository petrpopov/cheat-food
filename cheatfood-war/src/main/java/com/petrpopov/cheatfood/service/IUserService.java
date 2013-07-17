package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.UserCreate;
import com.petrpopov.cheatfood.model.UserEntity;

/**
 * User: petrpopov
 * Date: 17.07.13
 * Time: 22:59
 */
public interface IUserService {

    public UserEntity createUser(UserCreate user) throws CheatException;
    public UserEntity getUserByEmail(String email);
    public UserEntity getUserById(String id);
    public UserEntity getUserByFoursquareId(String foursquareId);
    public UserEntity getUserByFacebookId(String facebookId);
    public UserEntity getUserByCookieId(String cookie);
    public UserEntity saveOrUpdate(UserEntity userEntity);
}
