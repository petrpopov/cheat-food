package com.petrpopov.cheapfood.model;

import com.petrpopov.cheapfood.config.DateSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:01
 */

@Document(collection = "location")
public class Location {

    @Id
    private String id;

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    private String description;

    private String addressDescription;

    @Valid
    private GeoLocation geoLocation;

    @NotNull
    private Date actualDate;

    private String creatorId;
    private Address address;

    @NotNull
    private Boolean footype;

    @NotNull
    @NotEmpty
    private String type;

    public Location() {
    }

    public Location(String id, String title, String description, String addressDescription, Address address, GeoLocation geoLocation, Date actualDate, String creatorId, Boolean footype, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.addressDescription = addressDescription;
        this.address = address;
        this.geoLocation = geoLocation;
        this.actualDate = actualDate;
        this.creatorId = creatorId;
        this.footype = footype;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @JsonSerialize(using = DateSerializer.class)
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
