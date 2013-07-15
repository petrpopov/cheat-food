package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.FoursquareUserGroup;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class FriendInfoMixin {
	@JsonCreator
	FriendInfoMixin(
			@JsonProperty("count") int total,
			@JsonProperty("groups") List<FoursquareUserGroup> groups){}
}
