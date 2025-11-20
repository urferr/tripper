package com.embabel.tripper.agent.domain;

import java.util.List;
import java.util.stream.Collectors;

import com.embabel.common.ai.prompt.PromptContributor;

public record Travelers(List<Traveler> travelers) implements PromptContributor {

    @Override
    public String contribution() {
	if (travelers.isEmpty()) {
	    return "No information could be found about travelers";
	}
	else {
	    var aContribution = "%d travelers:\n%s".formatted(travelers.size(),
		    travelers.stream().map(theTraveler -> "%s: %s".formatted(theTraveler.name(), theTraveler.about()))
			    .collect(Collectors.joining("\n")));

	    return aContribution;
	}
    }

}
