package com.embabel.tripper;

import org.springframework.web.client.RestClient;

public abstract class BraveSearchService {
    private final RestClient restClient;
    private final String baseUrl;
    private final String apiKey;
    private final String name;
    private final String description;

    protected BraveSearchService(RestClient.Builder theRestClientBuilder, String theBaseUrl, String theApiKey,
	    String theName, String theDescription) {
	restClient = theRestClientBuilder.build();
	baseUrl = theBaseUrl;
	apiKey = theApiKey;
	name = theName;
	description = theDescription;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public BraveSearchResults search(WebSearchRequest theRequest) {
	var aResponse = restClient.get()
		.uri(uriBuilder -> uriBuilder.path(baseUrl).queryParam("q", theRequest.query())
			.queryParam("count", theRequest.count()).queryParam("offset", theRequest.offset()).build())
		.header("X-Subscription-Token", apiKey).header("Accept", "application/json").retrieve()
		.body(BraveResponse.class);

	if (aResponse == null) {
	    throw new RuntimeException("No response body");
	}
	return aResponse.toBraveSearchResults(theRequest);
    }

    public String searchRaw(WebSearchRequest request) {
	var aResponse = restClient.get()
		.uri(uriBuilder -> uriBuilder.path(baseUrl).queryParam("q", request.query())
			.queryParam("count", request.count()).queryParam("offset", request.offset()).build())
		.header("X-Subscription-Token", apiKey).header("Accept", "application/json").retrieve()
		.body(String.class);

	if (aResponse == null) {
	    throw new RuntimeException("No response body");
	}
	return aResponse;
    }
}
