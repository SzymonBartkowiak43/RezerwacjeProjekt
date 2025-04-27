package com.example.systemrezerwacji.domain.openinghoursmodule;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.example.systemrezerwacji.domain.openinghoursmodule.dto.OpeningHoursDto;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.salonmodule.dto.AddHoursResponseDto;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpeningHoursFacadeTest {

    private OpeningHoursRepository openingHoursRepository = new OpeningHoursRepositoryTestImpl();


    private OpeningHoursFacade openingHoursFacade;

    @BeforeEach
    void setUp() {
        OpeningHoursConfiguration configuration = new OpeningHoursConfiguration();
        openingHoursFacade = configuration.createForTest(openingHoursRepository);
    }

    @Test
    void should_return_success_response_when_service_returns_success() {
        // given
        List<OpeningHoursDto> hours = List.of(new OpeningHoursDto(1L, "MONDAY", LocalTime.of(8,0), LocalTime.of(16,0)));
        Salon salon = new Salon();

        // when
        AddHoursResponseDto result = openingHoursFacade.addOpeningHours(hours, salon);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.openingHours()).isNotEmpty();
    }

    @Test
    void should_return_failure_response_when_service_returns_non_success() {
        // given
        List<OpeningHoursDto> hours = List.of(new OpeningHoursDto(1L, "MONDAY", LocalTime.of(8,0), LocalTime.of(6,0)));
        Salon salon = new Salon();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            openingHoursFacade.addOpeningHours(hours, salon);
        });


        assertThat(exception.getMessage()).contains("Opening time cannot be after closing time");


    }

}