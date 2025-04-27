package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermWithDateDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.AvailableDatesReservationDto;
import com.example.systemrezerwacji.domain.reservationmodule.response.AvailableTermSearchCriteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public  class AvailableTermFinder {

    private final EmployeeFacade employeeFacade;

    AvailableTermFinder(EmployeeFacade employeeFacade) {
        this.employeeFacade = employeeFacade;
    }

    List<AvailableTermWithDateDto> findNearestAvailableTerms(
            AvailableTermSearchCriteria criteria,
            int maxTerms
    ) {
        List<AvailableTermWithDateDto> terms = new ArrayList<>();
        LocalDate currentDate = criteria.startDate();

        while (terms.size() < maxTerms) {
            List<AvailableTermDto> dailyTerms = getDailyAvailableTerms(criteria, currentDate);
            terms.addAll(mapToDateTerms(dailyTerms, currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return terms.stream().limit(maxTerms).toList();
    }

    private List<AvailableTermDto> getDailyAvailableTerms(
            AvailableTermSearchCriteria criteria,
            LocalDate date
    ) {
        return employeeFacade.getAvailableHours(
                new AvailableDatesReservationDto(
                        date,
                        criteria.employeeId(),
                        criteria.offerId()
                )
        );
    }

    private List<AvailableTermWithDateDto> mapToDateTerms(
            List<AvailableTermDto> terms,
            LocalDate date
    ) {
        return terms.stream()
                .map(term -> new AvailableTermWithDateDto(
                        term.startServices(),
                        term.endServices(),
                        date
                ))
                .toList();
    }
}