package com.petrpopov.cheapfood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:01
 */

@Document
public class Location {

    @Id
    private String id;
    private String description;
    private String addressDescription;
    private Address address;
    private GeoLocation geoLocation;
    private Date actualDate;
    private String creatorId;
    private Boolean footype;
    private String type;

    public Location() {
    }

    public Location(String id, String description, String addressDescription, Address address, GeoLocation geoLocation, Date actualDate, String creatorId, Boolean footype, String type) {
        this.id = id;
        this.description = description;
        this.addressDescription = addressDescription;
        this.address = address;
        this.geoLocation = geoLocation;
        this.actualDate = actualDate;
        this.creatorId = creatorId;
        this.footype = footype;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddressDescription() {
        return addressDescription;
    }

    public void setAddressDescription(String addressDescription) {
        this.addressDescription = addressDescription;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Date getActualDate() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate = actualDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Boolean getFootype() {
        return footype;
    }

    public void setFootype(Boolean footype) {
        this.footype = footype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
