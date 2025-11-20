package com.embabel.tripper.agent.domain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.embabel.agent.domain.library.HasContent;

public record TravelPlan(JourneyTravelBrief brief, ProposedTravelPlan proposal, List<Stay> stays, Travelers travelers)
	implements HasContent {

    @Override
    public String getContent() {
	var allDaysText = proposal.days().stream()
		.map(theDay -> "%s - %s".formatted(theDay.date().toString(), theDay.getStayingAt().toString()))
		.collect(Collectors.joining("\n"));

	return """
		    %s
		    %s
		    Days: %s
		    Map:
		    $journeyMapUrl
		    Pages:
		    ${proposal.pageLinks.joinToString("\n") { "${it.url} - ${it.summary}" }}
		    Images:
		    ${proposal.imageLinks.joinToString("\n") { "${it.url} - ${it.summary}" }}
		""".formatted(proposal.title(), proposal.plan(), allDaysText);
    }

    public String journeyMapUrl() {
	var encodedLocations = proposal.days().stream().filter(distinctByKey(theDay -> theDay.locationAndCountry()))
		.map(theDay -> URLEncoder.encode(theDay.locationAndCountry(), StandardCharsets.UTF_8))
		.collect(Collectors.toList());

	return (encodedLocations.size() == 1)
		? "https://www.google.com/maps/search/?api=1&query=%s".formatted(encodedLocations.get(0))
		: "https://www.google.com/maps/dir/%s"
			.formatted(encodedLocations.stream().collect(Collectors.joining("/")));
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {

	Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
