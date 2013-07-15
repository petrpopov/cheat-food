package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.Todo;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class TodosMixin {
	@JsonCreator
	TodosMixin(
			@JsonProperty("count") int count,
			@JsonProperty("items") List<Todo> items){}
}
