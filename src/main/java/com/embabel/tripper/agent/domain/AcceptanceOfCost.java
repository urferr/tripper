package com.embabel.tripper.agent.domain;

public record AcceptanceOfCost(boolean accepted) {
    public static AcceptanceOfCost ACCEPTED = new AcceptanceOfCost(true);
}
