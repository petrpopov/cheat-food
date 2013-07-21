package com.petrpopov.cheatfood.service.impl;

import com.petrpopov.cheatfood.model.*;
import com.petrpopov.cheatfood.service.ILocationService;
import com.petrpopov.cheatfood.service.ITypeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.geo.Box;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:51
 */

@Component
public class LocationService extends GenericService<Location> implements ILocationService {

    @Autowired
    private ITypeService typeService;

    private Logger logger = Logger.getLogger(LocationService.class);

    public LocationService() {
        super(Location.class);
    }

    @PostConstruct
    public void init() {
        Geospatial2dsphereIndex index = new Geospatial2dsphereIndex("geoLocation");
        IndexOperations indexOperations = op.indexOps(Location.class);
        indexOperations.ensureIndex(index);
    }

    @Override
    public List<Location> findAllInBounds(@Valid GeoJSONPointBounds bounds) {

        Box box = this.getBoxFromBounds(bounds);
        Query query = new Query(Criteria.where("geoLocation").within(box));

        return op.find(query, Location.class);
    }

    @Override
    public List<Location> findAllInDifference(@Valid GeoJSONPointBounds inBounds, GeoJSONPointBounds notInBounds) {

        /*
        if( notInBounds == null ) {
            return this.findAllInBounds(inBounds);
        }

        Box inBox = this.getBoxFromBounds(inBounds);
        Box notInBox = this.getBoxFromBounds(notInBounds);

        Criteria inCriteria = Criteria.where("geoLocation").within(inBox);
        Criteria notInCriteria = Criteria.where("geoLocation").within(notInBox).not();
        Criteria main = new Criteria().andOperator(notInCriteria, inCriteria);
        Query query = new Query(main);



        return op.find(query, Location.class);       */

        if( notInBounds == null ) {
            return this.findAllInBounds(inBounds);
        }

        //TODO: very shitty code !
        List<Location> inList = this.findAllInBounds(inBounds);
        List<Location> notInList = this.findAllInBounds(notInBounds);

        List<Location> res = new ArrayList<Location>();
        for (Location location : inList) {
            if( !notInList.contains(location) )
                res.add(location);
        }

        return res;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    public Location createOrSave(@Valid Location location, UserEntity userEntity) {

        Type savedType = getTypeForLocation(location);
        if( savedType != null ) {
            logger.info("Setting saved Type to location");
            location.setType(savedType);
        }

        location.setCreator(userEntity);

        logger.info("Saving location to database");

        return saveLocationObject(location);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER') and #location.creator.id==principal.username")
    public void deleteLocation(Location location) {
        logger.info("Deleting location from database by object");
        op.remove(location);
    }

    @Override
    public long getLocationsCountInBound(@Valid GeoJSONPointBounds bounds) {

        Box box = this.getBoxFromBounds(bounds);
        Query query = new Query(Criteria.where("geoLocation").within(box) );

        long count = op.count(query, Location.class);
        return count;
    }

    private Location saveLocationObject(Location location) {
        if( location.getId() != null ) {
            if( location.getId().isEmpty() ) {
                location.setId(null);
            }
        }
        op.save(location);
        return location;
    }

    private Type getTypeForLocation(Location location) {

        Type type = location.getType();
        if( type == null ) {
            return null;
        }

        String typeId = type.getId();
        if( typeId == null ) {
            return null;
        }
        else if( typeId.isEmpty() ) {
            return null;
        }

        Type savedType = typeService.findById(typeId);
        return savedType;
    }

    private Box getBoxFromBounds(GeoJSONPointBounds bounds) {
        GeoJSONPoint northEast = bounds.getNorthEast();
        GeoJSONPoint southWest = bounds.getSouthWest();
        Box box = new Box(southWest.getCoordinates(), northEast.getCoordinates());

        return box;
    }
}
