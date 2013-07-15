package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.CheckinComment;

import java.util.List;

abstract class CheckinCommentInfoMixin {
	@JsonCreator
	CheckinCommentInfoMixin(
			@JsonProperty("count") int count,
			@JsonProperty("items") List<CheckinComment> comments) {}
}
