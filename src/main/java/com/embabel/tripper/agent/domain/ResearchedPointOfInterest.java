package com.embabel.tripper.agent.domain;

import java.util.Collections;
import java.util.List;

import com.embabel.agent.domain.library.InternetResource;
import com.embabel.agent.domain.library.InternetResources;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ResearchedPointOfInterest(PointOfInterest pointOfInterest, String research, List<InternetResource> links,
	@JsonPropertyDescription("Links to videos, from YouTube or other") List<InternetResource> videoLinks,
	@JsonPropertyDescription("Links to images. Links must be the images themselves, not just links to them.") List<InternetResource> imageLinks)
	implements InternetResources {
    public List<InternetResource> links() {
	if (links == null) {
	    return Collections.emptyList();
	}
	return links;
    }

    public List<InternetResource> videoLinks() {
	if (videoLinks == null) {
	    return Collections.emptyList();
	}
	return videoLinks;
    }

    public List<InternetResource> imageLinks() {
	if (imageLinks == null) {
	    return Collections.emptyList();
	}
	return imageLinks;
    }

    @Override
    public List<InternetResource> getLinks() {
	return links();
    }

}
