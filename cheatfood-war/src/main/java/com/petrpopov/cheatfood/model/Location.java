package com.petrpopov.cheatfood.model;

import com.petrpopov.cheatfood.config.DateSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:01
 */

@Document(collection = "locations")
public class Location implements Serializable {

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
    @Indexed
    private Geo2DPoint geoLocation;

    @NotNull
    private Date actualDate;

    private Address address;

    @NotNull
    private Boolean footype;

    @Valid
    @DBRef
    private Type type;

    @DBRef
    private UserEntity creator;

    @Valid
    private List<Vote> votes;

    @Transient
    private Boolean alreadyVoted;

    @Valid
    private List<Rate> rates;

    @Transient
    private Boolean alreadyRated;

    private Double averageRate;

    public Location() {
    }

    public Location(String id, String title, String description, String addressDescription, Geo2DPoint geoLocation,
                    Date actualDate, Address address, Boolean footype, Type type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.addressDescription = addressDescription;
        this.geoLocation = geoLocation;
        this.actualDate = actualDate;
        this.address = address;
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

    public Geo2DPoint getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(Geo2DPoint geoLocation) {
        this.geoLocation = geoLocation;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getActualDate() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate = actualDate;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public Boolean getFootype() {
        return footype;
    }

    public void setFootype(Boolean footype) {
        this.footype = footype;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public Boolean getAlreadyVoted() {
        return alreadyVoted;
    }

    public void setAlreadyVoted(Boolean alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public Boolean getAlreadyRated() {
        return alreadyRated;
    }

    public void setAlreadyRated(Boolean alreadyRated) {
        this.alreadyRated = alreadyRated;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (id != null ? !id.equals(location.id) : location.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
