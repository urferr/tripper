package com.embabel.tripper.agent.domain;

import java.time.LocalDate;

public record PointOfInterest(String name, String description, String location, LocalDate fromDate, LocalDate toDate) {

}
