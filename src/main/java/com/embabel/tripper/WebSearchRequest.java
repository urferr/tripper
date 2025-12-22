package com.embabel.tripper;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record WebSearchRequest(String query, int count,
	@JsonPropertyDescription("Offset for pagination, defaults to 0, goes up by 1 for page size") int offset) {

}
