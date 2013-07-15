package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.Photo;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class VenuePhotosMixin {
	@JsonCreator
	VenuePhotosMixin(
			@JsonProperty("count") int count,
			@JsonProperty("items") List<Photo> items){}
}
