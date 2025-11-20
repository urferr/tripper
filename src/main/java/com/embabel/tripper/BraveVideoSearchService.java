package com.embabel.tripper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@ConditionalOnProperty("BRAVE_API_KEY")
@Service
public class BraveVideoSearchService extends BraveSearchService {
    public BraveVideoSearchService(RestClient.Builder theRestClientBuilder,
	    @Value("${BRAVE_API_KEY}") String theApiKey) {
	super(theRestClientBuilder, "https://api.search.brave.com/res/v1/videos/search", theApiKey,
		"Brave video search", "Search for videos with Brave");
    }
}
