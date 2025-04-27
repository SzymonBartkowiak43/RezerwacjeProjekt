package com.example.systemrezerwacji.domain.usermodule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;


class UserValidatorTest {
    private UserValidator validator;

    @BeforeEach
    public void setup() {
        validator = new UserValidator();
        validator.errors = new ArrayList<>();
    }

    @Test
    public void should_addEmptyNameError_when_nameIsNull() {
        // When
        validator.validName(null);

        // Then
        assertThat(validator.errors).contains(ValidationError.EMPTY_NAME);
    }

    @Test
    public void should_addEmptyNameError_when_nameIsEmpty() {
        // When
        validator.validName("   ");

        // Then
        assertThat(validator.errors).contains(ValidationError.EMPTY_NAME);
    }

    @Test
    public void should_addShortNameError_when_nameIsTooShort() {
        // When
        validator.validName("Ab");

        // Then
        assertThat(validator.errors).contains(ValidationError.SHORT_NAME);
    }

    @Test
    public void should_addForbiddenCharactersInNameError_when_nameContainsDigits() {
        // When
        validator.validName("Name123");

        // Then
        assertThat(validator.errors).contains(ValidationError.FORBIDDEN_CHARACTERS_IN_NAME);
    }

    @Test
    public void should_addEmptyEmailError_when_emailIsNull() {
        // When
        validator.validEmail(null);

        // Then
        assertThat(validator.errors).contains(ValidationError.EMPTY_EMAIL);
    }

    @Test
    public void should_addEmptyEmailError_when_emailIsEmpty() {
        // When
        validator.validEmail("   ");

        // Then
        assertThat(validator.errors).contains(ValidationError.EMPTY_EMAIL);
    }

    @Test
    public void should_addInvalidEmailError_when_emailIsInvalid() {
        // When
        validator.validEmail("invalid-email");

        // Then
        assertThat(validator.errors).contains(ValidationError.INVALID_EMAIL);
    }


    @Test
    public void should_addEmptyPasswordError_when_passwordIsNull() {
        // When
        validator.validPassword(null);

        // Then
        assertThat(validator.errors).contains(ValidationError.EMPTY_PASSWORD);
    }

    @Test
    public void should_addEmptyPasswordError_when_passwordIsEmpty() {
        // When
        validator.validPassword("   ");

        // Then
        assertThat(validator.errors).contains(ValidationError.EMPTY_PASSWORD);
    }

    @Test
    public void should_addShortPasswordError_when_passwordIsTooShort() {
        // When
        validator.validPassword("s");

        // Then
        assertThat(validator.errors).contains(ValidationError.SHORT_PASSWORD);
    }

    @Test
    public void should_addNoSpecialCharactersInPasswordError_when_passwordDoesNotContainSpecialCharacters() {
        // When
        validator.validPassword("Password123");

        // Then
        assertThat(validator.errors).contains(ValidationError.NO_SPECIAL_CHARACTERS_IN_PASSWORD);
    }
}