package com.example.systemrezerwacji.domain.openinghoursmodule;

import org.mockito.MockitoAnnotations;

public class OpeningHoursConfiguration {

    public OpeningHoursConfiguration() {
        MockitoAnnotations.openMocks(this);
    }

    OpeningHoursFacade createForTest(OpeningHoursRepository openingHoursRepository) {
        OpeningHoursService openingHoursService = new OpeningHoursService(openingHoursRepository);
        return new OpeningHoursFacade(openingHoursService);
    }

}
