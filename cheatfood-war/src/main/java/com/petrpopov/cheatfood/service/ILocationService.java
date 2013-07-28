package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.GeoPointBounds;
import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.model.Vote;

import java.util.List;

/**
 * User: petrpopov
 * Date: 12.07.13
 * Time: 12:53
 */
public interface ILocationService extends IGenericService<Location> {

    public List<Location> findAllInBounds(GeoPointBounds bounds);
    public List<Location> findAllTypeInBounds(GeoPointBounds bounds, String typeId);
    public List<Location> findAllInDifference(GeoPointBounds inBounds, GeoPointBounds notInBounds, String typeId);
    public Location createOrSave(Location location, UserEntity userEntity);
    public void deleteLocation(Location location);
    public long getLocationsCountInBound(GeoPointBounds bounds);
    public void voteForLocation(Location location, Vote vote) throws CheatException;
}
