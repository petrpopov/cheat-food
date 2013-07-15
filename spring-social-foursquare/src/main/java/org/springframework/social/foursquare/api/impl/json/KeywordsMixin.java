package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.foursquare.api.Keyword;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
abstract class KeywordsMixin {
    @JsonCreator
    KeywordsMixin(
            @JsonProperty("count") int count,
            @JsonProperty("items") List<Keyword> items){}
}
