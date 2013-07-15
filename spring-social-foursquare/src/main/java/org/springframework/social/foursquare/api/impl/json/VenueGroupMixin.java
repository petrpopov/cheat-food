package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.VenueGroupItem;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class VenueGroupMixin {
	@JsonCreator
	VenueGroupMixin(
			@JsonProperty("type") String type,
			@JsonProperty("name") String name,
			@JsonProperty("items") List<VenueGroupItem> items){}
}
