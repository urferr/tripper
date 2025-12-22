package com.embabel.tripper;

import java.util.List;

record BraveNewsSearchResponse(List<BraveSearchResult> results, Query query) implements BraveResponse {
    @Override
    public BraveSearchResults toBraveSearchResults(WebSearchRequest theRequest) {
	return new BraveSearchResults(theRequest, query, results());
    }

}
