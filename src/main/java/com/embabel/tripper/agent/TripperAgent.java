package com.embabel.tripper.agent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.annotation.WaitFor;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.prompt.ResponseFormat;
import com.embabel.common.util.StringTransformer;
import com.embabel.tripper.BraveImageSearchService;
import com.embabel.tripper.agent.domain.AcceptanceOfCost;
import com.embabel.tripper.agent.domain.AirbnbResultsLlmReturn;
import com.embabel.tripper.agent.domain.Day;
import com.embabel.tripper.agent.domain.ItineraryIdeas;
import com.embabel.tripper.agent.domain.JourneyTravelBrief;
import com.embabel.tripper.agent.domain.PointOfInterest;
import com.embabel.tripper.agent.domain.PointOfInterestFindings;
import com.embabel.tripper.agent.domain.ProposedTravelPlan;
import com.embabel.tripper.agent.domain.ResearchedPointOfInterest;
import com.embabel.tripper.agent.domain.Stay;
import com.embabel.tripper.agent.domain.TravelPlan;
import com.embabel.tripper.agent.domain.Travelers;
import com.embabel.tripper.agent.domain.TravelersAndBrief;
import com.embabel.tripper.config.ToolsConfig;
import com.embabel.tripper.util.ImageChecker;

/**
 * Overall flow: 1. Lookup travelers based on a travel brief. Brief may be about
 * exploring a location or a journey. 2. Find points of interest based on travel
 * brief, travelers and mapping data. 3. Research each point of interest to
 * gather detailed information.
 */
@Agent(description = "Make a detailed travel plan")
public class TripperAgent {
    private static final String WEATHER_TOOLS = "weather";
    private static final ResponseFormat HTML = ResponseFormat.Companion.getHTML();

    private final Logger logger = LoggerFactory.getLogger(TripperAgent.class);

    private final TripperConfig config;
    private final BraveImageSearchService braveImageSearch;

    public TripperAgent(TripperConfig theConfig, BraveImageSearchService theBraveImageSearch) {
	config = theConfig;
	braveImageSearch = theBraveImageSearch;
    }

    @Action
    public AcceptanceOfCost confirmExpensiveOperation(JourneyTravelBrief travelBrief, Travelers travelers,
	    OperationContext context) {
	// Confirmation is needed if we came through the MCP route
	var confirmationNeeded = context.last(TravelersAndBrief.class) != null;
	if (!confirmationNeeded) {
	    // Take it as a given
	    return AcceptanceOfCost.ACCEPTED;
	}

	// Otherwise, explicitly ask the user for confirmation
	return WaitFor.confirmation(AcceptanceOfCost.ACCEPTED,
		"Go ahead? Building a travel plan for %s will cost up to 20c".formatted(
			travelers.travelers().stream().map(it -> it.name()).collect(Collectors.joining(" and "))));
    }

    @Action
    public ItineraryIdeas findPointsOfInterest(JourneyTravelBrief travelBrief, Travelers travelers,
	    OperationContext context) {
	return context.ai().withLlm(config.getThinkerLlm()).withPromptElements(config.getPlanner(), travelers)
		.withTools(CoreToolGroups.WEB, CoreToolGroups.MAPS, CoreToolGroups.MATH, WEATHER_TOOLS).createObject("""
			    Consider the following travel brief for a journey from %s to %s.
			    %s
			    Find points of interest that are relevant to the travel brief and travelers.
			    Use mapping tools to consider appropriate order and put a rough date
			    range for each point of interest.
			    Consider likely weather
			""".formatted(travelBrief.from(), travelBrief.to(), travelBrief.contribution()),
			ItineraryIdeas.class);
    }

    @Action
    public PointOfInterestFindings researchPointsOfInterest(JourneyTravelBrief travelBrief, Travelers travelers,
	    ItineraryIdeas itineraryIdeas, AcceptanceOfCost confirmation, OperationContext context) {
	logger.info("Researching {} points of interest: {}", itineraryIdeas.pointsOfInterest().size(),
		itineraryIdeas.pointsOfInterest().stream().sorted(Comparator.comparing(PointOfInterest::name))
			.map(PointOfInterest::name).collect(Collectors.joining(", ")));
	var promptRunner = config.getResearcher().promptRunner(context)
		.withPromptElements(travelers, config.getToolCallControl())
		.withTools(CoreToolGroups.WEB, CoreToolGroups.BROWSER_AUTOMATION, WEATHER_TOOLS)
		.withToolObject(braveImageSearch);

	var poiFindings = context.parallelMap(itineraryIdeas.pointsOfInterest(), config.getMaxConcurrency(), poi -> {
	    var rpi = promptRunner.createObject("""
	    	    Research the following point of interest.
	    	    Consider interesting stories about art and culture and famous people.
	    	    Your audience: %s
	    	    Dates to consider: %s to %s
	    	    If any particularly important events are happening here during this time, mention them
	    	    and list specific dates.
	    	    Also consider likely weather.
	    	    <point-of-interest-to-research>
	    	    %s
	    	    %s
	    	    %s
	    	    Date: from &s to: %s
	    	    </point-of-interest-to-research>
	    	    Use the image search tool to find images of the point of interest.
	    	""".formatted(travelBrief.brief(), travelBrief.departureDate().toString(),
		    travelBrief.returnDate().toString(), poi.name(), poi.description(), poi.location(),
		    poi.fromDate().toString(), poi.toDate().toString()), ResearchedPointOfInterest.class);
	    return rpi;
	});

	return new PointOfInterestFindings(poiFindings);
    }

    /**
     * Use a good LLM to build a plan based on research.
     */
    @Action
    public ProposedTravelPlan proposeTravelPlan(JourneyTravelBrief travelBrief, Travelers travelers,
	    PointOfInterestFindings poiFindings, OperationContext context) {
	var poiText = poiFindings.pointsOfInterest().stream().map(poi -> """
			%s
			%s
			%s
			Images: %s
			Videos: %s
		""".formatted(poi.pointOfInterest().name(), poi.research(),
		poi.links().stream().map(theLink -> "%s: %s".formatted(theLink.getUrl(), theLink.getSummary()))
			.collect(Collectors.joining(", ")),
		poi.imageLinks().stream().map(theLink -> "%s: %s".formatted(theLink.getUrl(), theLink.getSummary()))
			.collect(Collectors.joining(", ")),
		poi.videoLinks().stream().map(theLink -> "%s: %s".formatted(theLink.getUrl(), theLink.getSummary()))
			.collect(Collectors.joining(", "))))
		.collect(Collectors.joining("\n"));

	return config.getPlanner().promptRunner(context)
		.withTools(CoreToolGroups.WEB, CoreToolGroups.MAPS, CoreToolGroups.MATH)
		.withPromptElements(travelers, HTML).createObject(
			"""
				Given the following travel brief, create a detailed plan.
				Give it a brief, catchy title that doesn't include dates,
				but may consider season, mood or relate to travelers's interests.

				Plan the journey to minimize travel time.
				However, consider any important events or places of interest along the way
				that might inform routing.
				Include total distances.

				<brief>%s</brief>
				Consider the weather in your recommendations. Use mapping tools to consider distance of driving or walking.

				Write up in %d words or less.
				Include links in text where appropriate and in the links field.

				Include the location for each day.
				The "locationAndCountry" field for each day should be in the format <location,+Country> e.g.
				Ghent,+Belgium
				If successive days are in the same town, just repeat the same location.

				Put image links where appropriate in text and also in the links field.
				Links must specify opening in a new window.
				IMPORTANT: Image links must have been provided by the researchers
				          and not be general knowledge or from other web sites.

				Recount at least one interesting story about a famous person
				associated with an area.

				Include natural headings and paragraphs in HTML format.
				Use unordered lists as appropriate.
				Start any headings at <h4>
				Embed images in text, with max width of %dpx.
				Be sure to include informative caption and alt text for each image.

				Consider the following points of interest:
				%s
				"""
				.formatted(travelBrief.contribution(), config.getWordCount(), config.getImageWidth(),
					poiText),
			ProposedTravelPlan.class);
    }

    @Action
    public TravelPlan findPlacesToSleep(JourneyTravelBrief brief, ProposedTravelPlan plan, Travelers travelers,
	    OperationContext context) {
	// Sanitize the content to ensure it is safe for display
	var stays = plan.days().stream().collect(Collectors.groupingBy(Day::getStayingAt)).entrySet().stream()
		.map(theEntry -> new Stay(theEntry.getValue(), null))
		.sorted(Comparator.comparing(it -> it.days().get(0).date())).collect(Collectors.toList());

	var dailyAccommodationBudget = brief.dailyBudget() / 2.0;

	var stayFinderPromptRunner = config.getResearcher().promptRunner(context).withPromptContributor(travelers)
		.withTools(ToolsConfig.AIRBNB, CoreToolGroups.MATH);

	var foundStays = context.parallelMap(stays, config.getMaxConcurrency(), stay -> {
	    logger.info("Finding Airbnb options for stay at: {}", stay.getLocationAndCountry());
	    var airbnbResults = stayFinderPromptRunner.createObject(
		    """
		    	    Find the Airbnb search URL for the following stay using the available tools.
		    	    Staying at location: %s
		    	    Dates: %s
		    	    You MUST set the 'ignoreRobotsText' parameter value to true for all calls to the airbnb API
		    	    Try to stay under the following daily budget (USD): %f
		    	    If no suitable options are found under that, return the cheapest available options.
		    	"""
			    .formatted(stay.getStayingAt(),
				    stay.days().stream().map(theDay -> theDay.date().toString())
					    .collect(Collectors.joining(", ")),
				    dailyAccommodationBudget),
		    AirbnbResultsLlmReturn.class);

	    return new Stay(stay.days(), airbnbResults.searchUrl());
	});

	return new TravelPlan(brief, plan, foundStays, travelers);
    }

    @AchievesGoal(description = "Create a detailed travel plan based on a given travel brief", export = @Export(name = "makeTravelPlan", remote = true, startingInputTypes = {
	    TravelersAndBrief.class }))
    @Action
    public TravelPlan postProcessHtml(TravelPlan plan) {
	var oldPlan = plan.proposal().plan();
	var oldProposal = plan.proposal();

	return new TravelPlan(plan.brief(),
		new ProposedTravelPlan(oldProposal.title(),
			StringTransformer.Companion.transform(oldPlan,
				List.of(styleImages, ImageChecker.removeInvalidImageLinks)),
			oldProposal.days(), oldProposal.imageLinks(), oldProposal.videoLinks(), oldProposal.pageLinks(),
			oldProposal.countriesVisited()),
		plan.stays(), plan.travelers());
    }

    private StringTransformer styleImages = html -> html.replace("<img", "<img class=\"styled-image-thick\"");

}
