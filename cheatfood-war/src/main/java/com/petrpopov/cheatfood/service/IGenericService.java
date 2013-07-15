package com.petrpopov.cheatfood.service;

import java.util.List;

/**
 * User: petrpopov
 * Date: 12.07.13
 * Time: 12:51
 */
public interface IGenericService<T> {

    public List<T> findAll();
    public T findById(String id);
}
