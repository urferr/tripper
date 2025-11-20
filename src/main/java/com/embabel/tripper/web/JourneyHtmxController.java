package com.embabel.tripper.web;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.Budget;
import com.embabel.agent.core.ProcessOptions;
import com.embabel.agent.core.Verbosity;
import com.embabel.agent.web.htmx.GenericProcessingValues;
import com.embabel.tripper.agent.domain.JourneyTravelBrief;
import com.embabel.tripper.agent.domain.Traveler;
import com.embabel.tripper.agent.domain.Travelers;

@Controller
@RequestMapping(value = { "/", "/travel/journey" })
public class JourneyHtmxController {
    private final AgentPlatform agentPlatform;

    private static record JourneyPlanForm(String from, String to, String transportPreference, String brief,
	    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
	    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate, double dailyBudget,
	    List<TravelerForm> travelers) {}

    private static record TravelerForm(String name, String about) {}

    public JourneyHtmxController(AgentPlatform theAgentPlatform) {
	agentPlatform = theAgentPlatform;
    }

    @GetMapping
    public String showPlanForm(Model theModel) {
	var aCurrentdate = LocalDate.now();

	theModel.addAttribute("travelBrief", new JourneyPlanForm("Barcelona", "Bordeaux", "driving",
		"Relaxed road trip exploring countryside, history, food and wine.", aCurrentdate,
		aCurrentdate.plus(Period.ofDays(10)), 200.0,
		List.of(new TravelerForm("Ingrid", "Loves history and museums. Fascinated by Joan of Arc."),
			new TravelerForm("Claude", "Enjoys food and wine. Has a particular interest in cabernet."))));
	return "journey-form";
    }

    @PostMapping("/plan")
    public String planJourney(@ModelAttribute JourneyPlanForm theForm, Model theModel) {
	var travelBrief = new JourneyTravelBrief(theForm.from, theForm.to, theForm.transportPreference, theForm.brief,
		theForm.departureDate, theForm.returnDate, theForm.dailyBudget);

	// Convert form travelers to domain objects
	var travelersList = theForm.travelers().stream()
		.map(theTravelerForm -> new Traveler(theTravelerForm.name(), theTravelerForm.about()))
		.collect(Collectors.toList());

	var travelers = new Travelers(travelersList);

	var agent = agentPlatform.agents().stream()
		.filter(theAgent -> theAgent.getName().toLowerCase().contains("trip")).findAny()
		.orElseThrow(() -> new IllegalStateException(
			"No travel agent found. Please ensure the tripper agent is registered."));

	Verbosity aVerbosity = Verbosity.DEFAULT.withShowPrompts(true).withShowLlmResponses(true);
	Budget aBudget = Budget.DEFAULT.withTokens(Budget.DEFAULT_TOKEN_LIMIT * 3);
	ProcessOptions aProcessOptions = ProcessOptions.DEFAULT.withVerbosity(aVerbosity).withBudget(aBudget);

	var agentProcess = agentPlatform.createAgentProcessFrom(agent, aProcessOptions, travelBrief, travelers);

	theModel.addAttribute("travelBrief", travelBrief);
	new GenericProcessingValues(agentProcess, "Planning your journey", travelBrief.brief(), "travelPlan",
		"journey-plan").addToModel(theModel);
	agentPlatform.start(agentProcess);
	return "common/processing";
    }
}
