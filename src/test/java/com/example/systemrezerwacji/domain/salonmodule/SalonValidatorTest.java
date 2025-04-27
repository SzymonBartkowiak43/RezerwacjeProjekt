package com.example.systemrezerwacji.domain.salonmodule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;


class SalonValidatorTest {
    private SalonValidator validator;

    @BeforeEach
    public void setup() {
        validator = new SalonValidator();
        validator.errors = new ArrayList<>();
    }

    @Test
    public void should_addEmptyNameError_when_nameIsNull() {
        // When
        validator.validateName(null);

        // Then
        assertThat(validator.errors).containsExactly(ValidationError.EMPTY_NAME);
    }

    @Test
    public void should_addShortNameError_when_nameIsTooShort() {
        // When
        validator.validateName("Ab");

        // Then
        assertThat(validator.errors).containsExactly(ValidationError.SHORT_NAME);
    }

    @Test
    public void should_addEmptyCategoryError_when_categoryIsNull() {
        // When
        validator.validateCategory(null);

        // Then
        assertThat(validator.errors).containsExactly(ValidationError.EMPTY_CATEGORY);
    }

    @Test
    public void should_addShortCategoryError_when_categoryIsTooShort() {
        // When
        validator.validateCategory("Ca");

        // Then
        assertThat(validator.errors).containsExactly(ValidationError.SHORT_CATEGORY);
    }

    @Test
    public void should_addForbiddenCharactersInCityError_when_cityContainsNumbers() {
        // When
        validator.validateCity("City123");

        // Then
        assertThat(validator.errors).containsExactly(ValidationError.FORBIDDEN_CHARACTERS_IN_CITY);
    }

    @Test
    public void should_addIncorrectZipCodeError_when_zipCodeIsInvalid() {
        // When
        validator.validateZipCode("12345");

        // Then
        assertThat(validator.errors).containsExactly(ValidationError.INCORRECT_ZIP_CODE);
    }


    @Test
    public void should_addEmptyNumberError_when_numberIsNull() {
        // When
        validator.validateNumber(null);

        // Then
        assertThat(validator.errors).containsExactly(ValidationError.EMPTY_NUMBER);
    }
}