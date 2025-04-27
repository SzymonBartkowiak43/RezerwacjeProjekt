package com.example.systemrezerwacji.domain.reservationmodule;


import com.example.systemrezerwacji.domain.reservationmodule.dto.CreateReservationDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.systemrezerwacji.domain.reservationmodule.ValidationError.*;

@Component
class ReservationValidator {
    private final ReservationService reservationService;
    private static final String ERROR_DELIMITER = ",";
    List<ValidationError> errors;


    ReservationValidator(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    ReservationValidationResult validate(CreateReservationDto reservationDto, LocalTime duration) {

        errors = new LinkedList<>();

        validateEmployeeId(reservationDto.employeeId());
        validateOfferId(reservationDto.offerId());
        validateSalonId(reservationDto.salonId());
        validateEmail(reservationDto.userEmail());
        validateTerm(reservationDto, duration);

        return errors.isEmpty() ? ReservationValidationResult.success() : ReservationValidationResult.failure(getFailureMessage());
    }


    void validateSalonId(Long salonId) {
        if(salonId == null) {
            errors.add(EMPTY_SALON_ID);
            return;
        }
        if(salonId <= 0) {
            errors.add(EMPTY_SALON_ID);
        }

    }

    void validateOfferId(Long offerId) {
        if(offerId == null) {
            errors.add(EMPTY_OFFER_ID);
            return;
        }
        if(offerId <= 0 ) {
            errors.add(EMPTY_OFFER_ID);
        }
    }

    void validateEmployeeId(Long employeeId) {
        if(employeeId == null) {
            errors.add(EMPTY_EMPLOYEE_ID);
            return;
        }
        if(employeeId <= 0) {
            errors.add(EMPTY_EMPLOYEE_ID);
        }
    }

    void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            errors.add(EMPTY_EMAIL);
            return;
        }
        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            errors.add(INVALID_EMAIL);
        }
    }


    void validateTerm(CreateReservationDto reservationDto, LocalTime duration) {
        if(reservationDto.reservationDateTime().isBefore(LocalDateTime.now())) {
            errors.add(PAST_DATE);
        }

        Long employeeId = reservationDto.employeeId();
        LocalDate dateOfReservation =  reservationDto.reservationDateTime().toLocalDate();
        LocalTime startTime = reservationDto.reservationDateTime().toLocalTime();
        LocalTime endTime = startTime.plusHours(duration.getHour()).plusMinutes(duration.getMinute());


        List<AvailableTermDto> employeeBusyTerms = reservationService.getEmployeeBusyTerms(employeeId ,dateOfReservation);

        boolean hasConflict = employeeBusyTerms.stream()
                .anyMatch(busyTerm -> {
                    LocalTime busyStart = busyTerm.startServices();
                    LocalTime busyEnd = busyStart.plusHours(duration.getHour())
                            .plusMinutes(duration.getMinute());
                    return startTime.isBefore(busyEnd) && endTime.isAfter(busyStart);
                });

        if(hasConflict) {
            errors.add(EMPLOYEE_IS_BUSY);
        }


    }

    private  String getFailureMessage() {
        return errors.stream()
                .map(ValidationError::getMessage)
                .collect(Collectors.joining(ERROR_DELIMITER));
    }

}
