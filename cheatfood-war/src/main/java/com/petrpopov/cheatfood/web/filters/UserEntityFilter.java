package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 27.08.13
 * Time: 15:20
 */

@Component
public class UserEntityFilter {

    public List<UserEntity> filterUserEntities(List<UserEntity> list) {

        if( list == null )
            return list;

        for (UserEntity entity : list) {
            filterUserEntity(entity);
        }

        return list;
    }

    public UserEntity filterUserEntity(UserEntity entity) {

        if( entity == null )
            return entity;

        entity.setVisibleName(getVisibleName(entity));
        entity.setPublicName(getPublicName(entity));

        return entity;
    }

    public String getVisibleName(UserEntity entity) {

        if( entity == null )
            return "";

        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();

        if( firstName == null && lastName == null ) {
            return entity.getEmail();
        }

        String name = firstName;

        if( firstName != null ) {
            name = firstName.trim();

            if( lastName != null ) {
                if( !lastName.trim().isEmpty() ) {
                    name += " " + lastName;
                }
            }
        }
        else {
            if( lastName != null ) {
                name = lastName.trim();
            }
        }

        return name;
    }

    public String getPublicName(UserEntity entity) {

        if( entity == null )
            return "";

        String visibleName = getVisibleName(entity);

        if( visibleName == null || entity.getEmail() == null )
            return "";

        if( !visibleName.equals(entity.getEmail()))
            return visibleName;

        //visibleName = email
        int index = visibleName.indexOf("@");
        if( index >= 0 )
            return visibleName.substring(0, index);

        String id = entity.getId();
        if( id != null )
            return id;

        return "";
    }
}
