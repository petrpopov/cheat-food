package com.petrpopov.cheatfood.service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.GeoPointBounds;
import com.petrpopov.cheatfood.model.entity.*;
import com.petrpopov.cheatfood.web.filters.LocationRateFilter;
import com.petrpopov.cheatfood.web.filters.LocationVoteFilter;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.geo.Box;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:51
 */

@Component
public class LocationService extends GenericService<Location> {

    @Autowired
    private TypeService typeService;

    @Autowired
    private LocationVoteFilter locationVoteFilter;

    @Autowired
    private LocationRateFilter locationRateFilter;

    @Autowired
    private UserService userService;

    @Value("#{properties.max_price}")
    private Double maxPrice;


    public LocationService() {
        super(Location.class);
        logger = Logger.getLogger(LocationService.class);
    }

    @PostConstruct
    public void init() {
        GeospatialIndex index = new GeospatialIndex("geoLocation");
        IndexOperations indexOperations = op.indexOps(Location.class);
        indexOperations.ensureIndex(index);

        batchUpdateAllCollectionObjects();
    }

    @Override
    public List<Location> findAll() {

        List<Location> all = super.findAll();
        return filterListForHiddenLocations(all);
    }

    public List<Location> findAllInBounds(@Valid GeoPointBounds bounds) {

        Box box = this.getBoxFromBounds(bounds);
        Query query = new Query(Criteria.where("geoLocation").within(box));

        List<Location> all = op.find(query, Location.class);
        return filterListForHiddenLocations(all);
    }

    public List<Location> findAllInBounds(@Valid GeoPointBounds bounds, String typeId) {

        if(typeId == null ) {
            return this.findAllInBounds(bounds);
        }

        if(typeId.isEmpty()) {
            return this.findAllInBounds(bounds);
        }

        Box box = this.getBoxFromBounds(bounds);
        Query query = new Query();

        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("geoLocation").within(box), Criteria.where("type.$id").is(new ObjectId(typeId)));
        query.addCriteria(criteria);

        List<Location> all = op.find(query, Location.class);
        return filterListForHiddenLocations(all);
    }

    public List<Location> findAllInDifference(@Valid GeoPointBounds inBounds, GeoPointBounds notInBounds, String typeId) {

        if( notInBounds == null ) {
            return this.findAllInBounds(inBounds, typeId);
        }

        if( notInBounds.equals(inBounds) ) {
            return this.findAllInBounds(inBounds, typeId);
        }

        Type type = null;
        if( typeId != null ) {
            if( !typeId.isEmpty() ) {
                type = typeService.findById(typeId);
            }
        }

        //TODO: very shitty code !
        List<Location> inList = type == null ? this.findAllInBounds(inBounds) : this.findAllInBounds(inBounds, typeId);
        List<Location> notInList = type == null ? this.findAllInBounds(notInBounds) :this.findAllInBounds(notInBounds, typeId);

        if( notInList.equals(inList) ) {
            return inList;
        }

        List<Location> res = new ArrayList<Location>();
        for (Location location : inList) {
            if( !notInList.contains(location) )
                res.add(location);
        }

        return filterListForHiddenLocations(res);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public Location createOrSave(@Valid Location location, UserEntity userEntity) throws CheatException{

        Double averagePrice = location.getAveragePrice();
        if( averagePrice > maxPrice )
            throw new CheatException(ErrorType.overpriced);

        Type savedType = getTypeForLocation(location);
        if( savedType != null ) {
            logger.info("Setting saved Type to location");
            location.setType(savedType);
        }

        this.setCreationDateForLocation(location);

        location.setCreator(userEntity);

        location.setVotes(this.getLocationVotes(location));
        location.setRates(this.getLocationRates(location));
        location.setVotesUpCount(this.getVotesUpCount(location));
        location.setVotesDownCount(this.getVotesDownCount(location));
        location.setAdminChecked(this.isAnyAdminHasVotedForLocation(location));
        location.setAverageRate(this.getNewAverageRate(location));


        logger.info("Saving location to database");

        return saveLocationObject(location);
    }

    @PreAuthorize("(hasRole('ROLE_USER') and #location.creator.id==principal.username) or hasRole('ROLE_ADMIN')")
    public void deleteLocation(Location location) {
        logger.info("Deleting location from database by object");
        op.remove(location);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void hideLocation(Location location) {
        logger.info("Hiding location from database by object");

        location.setHidden(true);
        op.save(location);
    }

    public long getLocationsTotalCount() {

        Criteria hidden = Criteria.where("hidden").ne(Boolean.TRUE);

        long count = op.count(new Query(hidden), Location.class);
        return count;
    }

    public long getLocationsCountInBound(@Valid GeoPointBounds bounds) {

        Box box = this.getBoxFromBounds(bounds);
        Criteria hidden = Criteria.where("hidden").ne(Boolean.TRUE);
        Query query = new Query(Criteria.where("geoLocation").within(box).andOperator(hidden) );

        long count = op.count(query, Location.class);
        return count;
    }

    public long getLocationsNewCount() {

        DateTime date = new DateTime(new Date());
        date = date.minusDays(1);

        Criteria dateCriteria = Criteria.where("creationDate").gte(date.toDate());

        long count = op.count(new Query(dateCriteria), Location.class);
        return count;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public Location voteForLocation(Location location, Vote vote) throws CheatException {

        List<Vote> votes = location.getVotes();
        UserEntity userById = userService.getUserById(vote.getUserId());
        boolean isAdmin = userService.isUserAdmin(userById);

        if( votes != null ) {
            boolean canVote = locationVoteFilter.canUserVoteForLocation(location, userById);

            if( !canVote )
                throw new CheatException(ErrorType.already_voted);
        }
        else {
            votes = new ArrayList<Vote>();
            location.setVotes(votes);
        }

        if( vote.getDate() == null ) {
            vote.setDate(new Date());
        }

        votes.add(vote);

        long votesUpCount = this.getVotesUpCount(location);
        long votesDownCount = this.getVotesDownCount(location);
        location.setVotesUpCount(votesUpCount);
        location.setVotesDownCount(votesDownCount);

        Boolean adminChecked = location.getAdminChecked();
        if( adminChecked == null ) {
            location.setAdminChecked(isAdmin);
        }
        else {
            if( adminChecked.equals(Boolean.FALSE) ) {
                location.setAdminChecked(isAdmin);
            }
        }

        return saveLocationObject(location);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public Location rateForLocation(Location location, Rate rate) throws CheatException {

        List<Rate> rates = location.getRates();
        if( rates != null ) {
            UserEntity userById = userService.getUserById(rate.getUserId());
            boolean canRate = locationRateFilter.canUserRateForLocation(location, userById);

            if( !canRate )
                throw new CheatException(ErrorType.already_rated);
        }
        else {
            rates = new ArrayList<Rate>();
            location.setRates(rates);
        }

        if( rate.getDate() == null ) {
            rate.setDate(new Date());
        }

        rates.add(rate);
        location.setAverageRate(getNewAverageRate(rates));
        return saveLocationObject(location);
    }


    @Override
    protected void updateEntityForBatchOperation(DBCollection collection, BasicDBObject entity) {

        updateLocationWithCurrentVotesDate(entity);
        updateLocationWithCurrentRatesDate(entity);
        updateLocationWithVotesCount(entity);
        updateLocationWithAdminChecked(entity);
        updateLocationWithCreationDate(entity);

        collection.save(entity);
    }

    private void updateLocationWithCurrentVotesDate(BasicDBObject entity) {

        Object votes = entity.get("votes");
        if( votes == null ) {
            return;
        }

        if( !(votes instanceof BasicDBList) ) {
            return;
        }

        BasicDBList votesList = (BasicDBList) votes;
        for (Object o : votesList) {
            if( !(o instanceof BasicDBObject) )
                continue;

            BasicDBObject obj = (BasicDBObject) o;
            Object date = obj.get("date");
            if( date == null ) {
                logger.info("Updating location " + entity.get("_id") + " setting today date for votes");
                obj.put("date", new Date());
            }
        }
    }

    private void updateLocationWithCurrentRatesDate(BasicDBObject entity) {

        Object rates = entity.get("rates");
        if( rates == null ) {
            return;
        }

        if( !(rates instanceof BasicDBList) ) {
            return;
        }

        BasicDBList votesList = (BasicDBList) rates;
        for (Object o : votesList) {
            if( !(o instanceof BasicDBObject) )
                continue;

            BasicDBObject obj = (BasicDBObject) o;
            Object date = obj.get("date");
            if( date == null ) {
                logger.info("Updating location " + entity.get("_id") + " setting today date for rates");
                obj.put("date", new Date());
            }
        }
    }

    private void updateLocationWithVotesCount(BasicDBObject entity) {

        Object votes = entity.get("votes");
        if( votes == null ) {
            return;
        }

        if( !(votes instanceof BasicDBList) ) {
            return;
        }

        int upCount = 0;
        int downCount = 0;
        BasicDBList votesList = (BasicDBList) votes;
        for (Object obj : votesList) {
            if( !(obj instanceof BasicDBObject) )
                continue;

            BasicDBObject vote = (BasicDBObject) obj;
            Object value = vote.get("value");

            if( !(value instanceof Boolean) )
                continue;

            Boolean val = (Boolean) value;
            if( val.equals(Boolean.TRUE) )
                upCount++;
            else
                downCount++;
        }

        entity.put("votesUpCount", upCount);
        entity.put("votesDownCount", downCount);
    }

    private void updateLocationWithAdminChecked(BasicDBObject entity) {

        Object votes = entity.get("votes");
        if( votes == null ) {
            return;
        }

        if( !(votes instanceof BasicDBList) ) {
            return;
        }

        boolean admin = false;
        BasicDBList votesList = (BasicDBList) votes;
        for (Object obj : votesList) {
            if( !(obj instanceof BasicDBObject) )
                continue;

            BasicDBObject vote = (BasicDBObject) obj;
            Object value = vote.get("value");

            if( !(value instanceof Boolean) )
                continue;

            Boolean val = (Boolean) value;
            if( val.equals(Boolean.FALSE) )
                continue;

            String userId = (String) vote.get("userId");
            boolean isAdmin = userService.isUserAdmin(userId);
            if( isAdmin == true ) {
                admin = true;
                break;
            }
        }

        entity.put("adminChecked", admin);
    }

    private void updateLocationWithCreationDate(BasicDBObject entity) {

        Object creationDate = entity.get("creationDate");
        if( creationDate != null ) {
            return;
        }

        entity.put("creationDate", new Date());
    }


    private List<Location> filterListForHiddenLocations(List<Location> all) {

        List<Location> res = new ArrayList<Location>();

        for (Location location : all) {
            Boolean hidden = location.getHidden();
            if( hidden != null ) {
                if( hidden.equals(Boolean.TRUE) ) {
                    continue;
                }
            }

            res.add(location);
        }


        return res;
    }

    private Double getNewAverageRate(Location location) {
        List<Rate> rates = location.getRates();
        return getNewAverageRate(rates);
    }

    private Double getNewAverageRate(List<Rate> rates) {

        if( rates == null )
            return 0.0;

        Double rate = 0.0;
        for (Rate rate1 : rates) {
            rate += rate1.getValue();
        }

        rate /= rates.size();
        return rate;
    }

    private List<Vote> getLocationVotes(Location location) {
        Location location1 = this.findById(location.getId());
        if( location1 == null )
            return null;

        return location1.getVotes();
    }

    private List<Rate> getLocationRates(Location location) {
        Location byId = this.findById(location.getId());
        if( byId == null )
            return null;

        return byId.getRates();
    }

    private long getVotesUpCount(Location location) {

        List<Vote> votes = location.getVotes();
        if( votes == null )
            return 0;

        long count = 0;
        for (Vote vote : votes) {
            if( vote == null )
                continue;

            if( vote.getValue().equals(Boolean.TRUE) )
                count++;
        }

        return count;
    }

    private long getVotesDownCount(Location location) {

        List<Vote> votes = location.getVotes();
        if( votes == null )
            return 0;

        long count = 0;
        for (Vote vote : votes) {
            if( vote == null )
                continue;

            if( vote.getValue().equals(Boolean.FALSE) )
                count++;
        }

        return count;
    }

    private boolean isAnyAdminHasVotedForLocation(Location location) {

        List<Vote> votes = location.getVotes();
        if( votes == null )
            return false;

        for (Vote vote : votes) {
            String userId = vote.getUserId();
            if( userId == null )
                continue;

            boolean userAdmin = userService.isUserAdmin(userId);
            if( userAdmin == true )
                return true;
        }

        return false;
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

    private void setCreationDateForLocation(Location location) {

        Location byId = this.findById(location.getId());
        if( byId == null )
            return;

        Date creationDate = byId.getCreationDate();
        if( creationDate == null )
            location.setCreationDate(new Date());
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

    private Box getBoxFromBounds(GeoPointBounds bounds) {
        Geo2DPoint northEast = bounds.getNorthEast2DPoint();
        Geo2DPoint southWest = bounds.getSouthWest2DPoint();

        Box box = new Box(southWest.getCoordinates(), northEast.getCoordinates());

        return box;
    }
}
