package com.embabel.tripper;

record BraveWebSearchResponse(WebResults web, Query query) implements BraveResponse {
    @Override
    public BraveSearchResults toBraveSearchResults(WebSearchRequest theRequest) {
	return new BraveSearchResults(theRequest, query, web.results());
    }

}
