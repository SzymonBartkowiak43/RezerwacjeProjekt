package com.example.systemrezerwacji.domain.reservationmodule.response;

import java.time.LocalDate;

public record AvailableTermSearchCriteria(
        Long employeeId,
        Long offerId,
        LocalDate startDate
) {}
