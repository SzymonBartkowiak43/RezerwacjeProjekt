package com.example.systemrezerwacji.domain.usermodule;


import com.example.systemrezerwacji.domain.usermodule.dto.UserRegisterDto;
import org.springframework.stereotype.Component;


import static com.example.systemrezerwacji.domain.usermodule.UserValidationResult.*;
import static com.example.systemrezerwacji.domain.usermodule.UserValidationResult.success;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
class UserValidator {
    private static final String ERROR_DELIMITER = ",";
    List<ValidationError> errors;



    UserValidationResult validate(UserRegisterDto user) {

        errors = new ArrayList<>();

        validName(user.name());
        validEmail(user.email());
        validPassword(user.password());


        return errors.isEmpty() ? success() : failure(getFailureMessage());
    }



    void validName(String name) {
        if (name == null || name.trim().isEmpty()) {
            errors.add(ValidationError.EMPTY_NAME);
            return;
        }
        if (name.length() < 3) {
            errors.add(ValidationError.SHORT_NAME);
        }
        if (!name.matches("[a-zA-Z]+")) {
            errors.add(ValidationError.FORBIDDEN_CHARACTERS_IN_NAME);
        }
    }

    void validEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            errors.add(ValidationError.EMPTY_EMAIL);
            return;
        }
        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            errors.add(ValidationError.INVALID_EMAIL);
        }
    }

    void validPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            errors.add(ValidationError.EMPTY_PASSWORD);
            return;
        }
        if (password.length() < 4) {
            errors.add(ValidationError.SHORT_PASSWORD);
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            errors.add(ValidationError.NO_SPECIAL_CHARACTERS_IN_PASSWORD);
        }
    }

    private  String getFailureMessage() {
        return errors.stream()
                .map(ValidationError::getMessage)
                .collect(Collectors.joining(ERROR_DELIMITER));
    }

}
