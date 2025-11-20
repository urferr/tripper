package com.embabel.tripper.agent.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record Day(LocalDate date,
	@JsonPropertyDescription("Location where the traveler will stay on this day in Google Maps friendly format 'City,+Country'") String locationAndCountry) {
    public String getStayingAt() {
	String[] allTokens = locationAndCountry.split(",");

	if (allTokens.length > 0 && !allTokens[0].trim().isEmpty()) {
	    return allTokens[0].trim();
	}
	else {
	    return "Unknown location";
	}
    }
}
