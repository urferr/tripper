package com.embabel.tripper;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@ConditionalOnProperty("BRAVE_API_KEY")
@Service
public class BraveImageSearchService extends BraveSearchService {
    public BraveImageSearchService(RestClient.Builder theRestClientBuilder,
	    @Value("${BRAVE_API_KEY}") String theApiKey) {
	super(theRestClientBuilder, "https://api.search.brave.com/res/v1/images/search", theApiKey,
		"Brave image search", "Search for images with Brave");
    }

    @Tool(description = "Brave image search")
    public String searchImages(WebSearchRequest theRequest) {
	var raw = searchRaw(theRequest);
	return raw;
    }
}
