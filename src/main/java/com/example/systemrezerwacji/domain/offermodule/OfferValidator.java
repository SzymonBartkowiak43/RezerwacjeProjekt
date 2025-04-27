package com.example.systemrezerwacji.domain.offermodule;


import com.example.systemrezerwacji.domain.offermodule.dto.CreateOfferDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.systemrezerwacji.domain.offermodule.OfferValidationResult.failure;
import static com.example.systemrezerwacji.domain.offermodule.OfferValidationResult.success;
import static com.example.systemrezerwacji.domain.offermodule.ValidationError.*;

@Component
class OfferValidator {

    private final static  String ERROR_DELIMITER = ",";
    List<ValidationError> errors;

    OfferValidationResult validate(CreateOfferDto createOfferDto) {

        errors = new LinkedList<>();

        validateName(createOfferDto.name());
        validateDescription(createOfferDto.description());
        validatePrice(createOfferDto.price());
        validateDuration(createOfferDto.duration());


        if(errors.isEmpty()) {
            return success();
        }

        String message = errors.stream()
                .map(errors -> errors.getMessage())
                .collect(Collectors.joining(ERROR_DELIMITER));
        return failure(message);
    }

    private void validateName(String name) {
        if(name == null) {
            errors.add(NAME_IS_EMPTY);
            return;
        }
        if(name.length() <= 3) {
            errors.add(NAME_IS_EMPTY);
        }
        if(name.length() > 100) {
            errors.add(NAME_TOO_LONG);
        }
    }

    private void validateDescription(String description) {
        if(description.length() >= 1024) {
            errors.add(DESCRIPTION_TOO_LONG);
        }
    }

    private void validatePrice(BigDecimal price) {
        if(price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(PRICE_IS_INVALID);
        }
    }

    private void validateDuration(LocalTime duration) {
        if(duration.isBefore(LocalTime.of(0,1))) {
            errors.add(DURATION_IS_INVALID);
        }
    }
}
