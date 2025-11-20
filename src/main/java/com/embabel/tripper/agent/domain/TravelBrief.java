package com.embabel.tripper.agent.domain;

import java.time.LocalDate;

import com.embabel.common.ai.prompt.PromptContributor;

public sealed interface TravelBrief extends PromptContributor permits JourneyTravelBrief {
    String brief();

    LocalDate departureDate();

    LocalDate returnDate();

    double dailyBudget();
}
