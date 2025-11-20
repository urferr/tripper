package com.embabel.tripper.agent.domain;

import java.time.LocalDate;

public record JourneyTravelBrief(String from, String to, String transportPreference, String brief,
	LocalDate departureDate, LocalDate returnDate, double dailyBudget) implements TravelBrief {

    @Override
    public String contribution() {
	return """
		    Journey from %s to %s
		    Dates: %s to %s
		    Brief: %s
		    Transport preference: %s
		""".formatted(from, to, departureDate.toString(), returnDate.toString(), brief, transportPreference);
    }
}