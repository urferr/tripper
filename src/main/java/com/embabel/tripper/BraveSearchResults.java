package com.embabel.tripper;

import java.time.Instant;
import java.util.List;

public record BraveSearchResults(WebSearchRequest request, Query query, List<BraveSearchResult> results,
	Instant timestamp, String id) {
    public BraveSearchResults(WebSearchRequest request, Query query, List<BraveSearchResult> results) {
	this(request, query, results, Instant.now(), null);
    }

    String getName() {
	return "Brave search results for query: %s".formatted(query.original());
    }
}
