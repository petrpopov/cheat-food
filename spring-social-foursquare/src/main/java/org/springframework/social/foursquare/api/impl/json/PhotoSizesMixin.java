package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.PhotoSize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class PhotoSizesMixin {
	@JsonCreator
	PhotoSizesMixin(
			@JsonProperty("count") int count,
			@JsonProperty("items") List<PhotoSize> items){}
}
