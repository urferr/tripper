package com.embabel.tripper.agent.domain;

import java.util.List;

import com.embabel.agent.domain.library.InternetResource;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ProposedTravelPlan(
	@JsonPropertyDescription("Catchy title appropriate to the travelers and travel brief") String title,
	@JsonPropertyDescription("Detailed travel plan") String plan,
	@JsonPropertyDescription("List of days in the travel plan") List<Day> days,
	@JsonPropertyDescription("Links to images") List<InternetResource> imageLinks,
	@JsonPropertyDescription("Links to videos") List<InternetResource> videoLinks,
	@JsonPropertyDescription("Links to pages with more information about the travel plan") List<InternetResource> pageLinks,
	@JsonPropertyDescription("List of country names that the travelers will visit") List<String> countriesVisited) {

}
