package com.embabel.tripper.agent.domain;

import java.util.List;

public record Stay(List<Day> days, String airbnbUrl) {
    public String getStayingAt() {
	if (days.isEmpty()) {
	    return "Unknown location";
	}
	return days.get(0).getStayingAt();
    }

    public String getLocationAndCountry() {
	if (days.isEmpty()) {
	    return "Unknown location";
	}
	return days.get(0).locationAndCountry();
    }

}
