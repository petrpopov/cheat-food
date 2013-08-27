package com.petrpopov.cheatfood.model.data;

import com.petrpopov.cheatfood.model.entity.UserEntity;

/**
 * User: petrpopov
 * Date: 27.08.13
 * Time: 19:08
 */
public class UserEntityInfo {

    private UserEntity userEntity;
    private Boolean savedNew;

    public UserEntityInfo(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.savedNew = Boolean.TRUE;
    }

    public UserEntityInfo(UserEntity userEntity, Boolean savedNew) {
        this.userEntity = userEntity;
        this.savedNew = savedNew;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Boolean getSavedNew() {
        return savedNew;
    }

    public void setSavedNew(Boolean savedNew) {
        this.savedNew = savedNew;
    }
}
