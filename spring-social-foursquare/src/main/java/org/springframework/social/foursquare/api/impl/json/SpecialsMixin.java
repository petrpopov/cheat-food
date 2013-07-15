package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.Special;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class SpecialsMixin {
	@JsonCreator
	SpecialsMixin(
			@JsonProperty("count") int count,
			@JsonProperty("items") List<Special> items){}
}
