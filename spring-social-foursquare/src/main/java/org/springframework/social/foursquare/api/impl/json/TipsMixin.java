package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.Tip;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class TipsMixin {
	@JsonCreator
	TipsMixin(
			@JsonProperty("count") int count,
			@JsonProperty("items") List<Tip> items){}
}
