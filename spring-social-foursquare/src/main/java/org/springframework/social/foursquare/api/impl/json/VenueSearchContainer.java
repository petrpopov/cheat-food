package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.social.foursquare.api.Venue;

import java.util.List;

@JsonDeserialize(using=VenueSearchContainerDeserializer.class)
public class VenueSearchContainer {
    
    List<Venue> venues;
    
    public VenueSearchContainer(List<Venue> venues) {
        this.venues = venues;
    }

    public List<Venue> getVenues() {
        return venues;
    }
    
}
