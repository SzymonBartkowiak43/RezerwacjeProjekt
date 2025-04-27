package com.example.systemrezerwacji.domain.salonmodule;

import com.example.systemrezerwacji.domain.salonmodule.dto.CreateNewSalonDto;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
class SalonValidator {

    private static final String ERROR_DELIMITER = ",";
    List<ValidationError> errors;


    SalonValidationResult validate(CreateNewSalonDto salonDto) {

        errors = new LinkedList<>();

        validateName(salonDto.salonName());
        validateCategory(salonDto.category());
        validateCity(salonDto.city());
        validateZipCode(salonDto.zipCode());
        validateStreet(salonDto.street());
        validateNumber(salonDto.number());


        if (errors.isEmpty()) {
            return SalonValidationResult.success();
        }

        String message = errors.stream()
                .map(error -> error.message)
                .collect(Collectors.joining(ERROR_DELIMITER));
        return SalonValidationResult.failure(message);

    }

    void validateName(String name) {
        if (name == null) {
            errors.add(ValidationError.EMPTY_NAME);
            return;
        }
        if (name.length() <= 3) {
            errors.add(ValidationError.SHORT_NAME);
        }

    }

    void validateCategory(String category) {
        if (category == null) {
            errors.add(ValidationError.EMPTY_CATEGORY);
            return;
        }
        if (category.length() <= 3) {
            errors.add(ValidationError.SHORT_CATEGORY);
        }
    }

    void validateCity(String city) {
        if (city == null) {
            errors.add(ValidationError.EMPTY_CITY);
            return;
        }
        if (!city.matches("^[a-zA-Z\\s]+$")) {
            errors.add(ValidationError.FORBIDDEN_CHARACTERS_IN_CITY);
        }
    }

    void validateZipCode(String zipCode) {
        if (zipCode == null) {
            errors.add(ValidationError.EMPTY_ZIP_CODE);
            return;
        }
        if (!zipCode.matches("\\d{2}-\\d{3}")) {
            errors.add(ValidationError.INCORRECT_ZIP_CODE);
        }
    }

    void validateStreet(String street) {
        if (street == null) {
            errors.add(ValidationError.EMPTY_STREET);
        }
    }

    void validateNumber(String number) {
        if (number == null) {
            errors.add(ValidationError.EMPTY_NUMBER);
        }
    }


}
