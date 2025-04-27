package com.example.systemrezerwacji.domain.offermodule;

import com.example.systemrezerwacji.domain.offermodule.dto.CreateOfferDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;


class OfferValidatorTest {

    OfferValidator offerValidator;

    @BeforeEach
    void init() {
        offerValidator = new OfferValidator();
    }

    @Test
    void should_return_success_message() {
        //given
        String name = "Thai massage";
        String description = "awesome massage";
        BigDecimal price = BigDecimal.valueOf(220.00);
        LocalTime duration = LocalTime.of(1, 0);
        Integer salonId = 1;

        CreateOfferDto createOfferDto = new CreateOfferDto(name, description, price, duration, salonId);

        //when
        OfferValidationResult validate = offerValidator.validate(createOfferDto);

        //then
        assertThat(validate.validationMessage()).isEqualTo("success");
    }

    @Test
    void should_return_error_because_name_is_empty() {
        //given
        String name = "";
        String description = "awesome massage";
        BigDecimal price = BigDecimal.valueOf(220.00);
        LocalTime duration = LocalTime.of(1, 0);
        Integer salonId = 1;

        CreateOfferDto createOfferDto = new CreateOfferDto(name, description, price, duration, salonId);

        //when
        OfferValidationResult validate = offerValidator.validate(createOfferDto);

        //then
        assertThat(validate.validationMessage()).isEqualTo(ValidationError.NAME_IS_EMPTY.getMessage());
    }

    @Test
    void should_return_error_because_name_is_short() {
        //given
        String name = "ab";
        String description = "awesome massage";
        BigDecimal price = BigDecimal.valueOf(220.00);
        LocalTime duration = LocalTime.of(1, 0);
        Integer salonId = 1;

        CreateOfferDto createOfferDto = new CreateOfferDto(name, description, price, duration, salonId);

        //when
        OfferValidationResult validate = offerValidator.validate(createOfferDto);

        //then
        assertThat(validate.validationMessage()).isEqualTo(ValidationError.NAME_IS_EMPTY.getMessage());
    }

    @Test
    void should_return_error_because_name_is_too_long() {
        //given
        String name = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String description = "awesome massage";
        BigDecimal price = BigDecimal.valueOf(220.00);
        LocalTime duration = LocalTime.of(1, 0);
        Integer salonId = 1;

        CreateOfferDto createOfferDto = new CreateOfferDto(name, description, price, duration, salonId);

        //when
        OfferValidationResult validate = offerValidator.validate(createOfferDto);

        //then
        assertThat(validate.validationMessage()).isEqualTo(ValidationError.NAME_TOO_LONG.getMessage());
    }

    @Test
    void should_return_error_because_price_is_invalid() {
        //given
        String name = "Massage";
        String description = "Int";
        BigDecimal price = BigDecimal.valueOf(00.00);
        LocalTime duration = LocalTime.of(1, 0);
        Integer salonId = 1;

        CreateOfferDto createOfferDto = new CreateOfferDto(name, description, price, duration, salonId);

        //when
        OfferValidationResult validate = offerValidator.validate(createOfferDto);

        //then
        assertThat(validate.validationMessage()).isEqualTo(ValidationError.PRICE_IS_INVALID.getMessage());
    }



}