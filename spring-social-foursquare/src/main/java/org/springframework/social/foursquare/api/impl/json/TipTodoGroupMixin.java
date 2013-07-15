package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.TipUserGroup;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class TipTodoGroupMixin {
	TipTodoGroupMixin(
			@JsonProperty("count") int count,
			@JsonProperty("groups") List<TipUserGroup> groups){}
}
