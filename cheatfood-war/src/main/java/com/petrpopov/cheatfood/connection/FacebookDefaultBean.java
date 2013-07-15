package com.petrpopov.cheatfood.connection;

import org.springframework.social.facebook.api.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import java.util.List;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 14:39
 */
public class FacebookDefaultBean implements Facebook {
    @Override
    public CommentOperations commentOperations() {
        return null;
    }

    @Override
    public EventOperations eventOperations() {
        return null;
    }

    @Override
    public FeedOperations feedOperations() {
        return null;
    }

    @Override
    public FriendOperations friendOperations() {
        return null;
    }

    @Override
    public GroupOperations groupOperations() {
        return null;
    }

    @Override
    public LikeOperations likeOperations() {
        return null;
    }

    @Override
    public MediaOperations mediaOperations() {
        return null;
    }

    @Override
    public PageOperations pageOperations() {
        return null;
    }

    @Override
    public PlacesOperations placesOperations() {
        return null;
    }

    @Override
    public UserOperations userOperations() {
        return null;
    }

    @Override
    public RestOperations restOperations() {
        return null;
    }

    @Override
    public boolean isAuthorized() {
        return false;
    }

    @Override
    public <T> T fetchObject(String s, Class<T> tClass) {
        return null;
    }

    @Override
    public <T> T fetchObject(String s, Class<T> tClass, MultiValueMap<String, String> stringStringMultiValueMap) {
        return null;
    }

    @Override
    public <T> List<T> fetchConnections(String s, String s2, Class<T> tClass, String... strings) {
        return null;
    }

    @Override
    public <T> List<T> fetchConnections(String s, String s2, Class<T> tClass, MultiValueMap<String, String> stringStringMultiValueMap) {
        return null;
    }

    @Override
    public byte[] fetchImage(String s, String s2, ImageType imageType) {
        return new byte[0];
    }

    @Override
    public String publish(String s, String s2, MultiValueMap<String, Object> stringObjectMultiValueMap) {
        return null;
    }

    @Override
    public void post(String s, String s2, MultiValueMap<String, String> stringStringMultiValueMap) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void delete(String s, String s2) {

    }
}
