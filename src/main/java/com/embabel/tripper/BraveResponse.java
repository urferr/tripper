package com.embabel.tripper;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({ @JsonSubTypes.Type(value = BraveWebSearchResponse.class),
	@JsonSubTypes.Type(value = BraveNewsSearchResponse.class) })
interface BraveResponse {
    Query query();

    BraveSearchResults toBraveSearchResults(WebSearchRequest theRequest);
}
