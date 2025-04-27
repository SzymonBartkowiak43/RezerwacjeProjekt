package com.example.systemrezerwacji.domain.openinghoursmodule;

import com.example.systemrezerwacji.domain.openinghoursmodule.dto.OpeningHoursDto;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.salonmodule.dto.AddHoursResponseDto;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OpeningHoursFacade {
    private final OpeningHoursService openingHoursService;


    public OpeningHoursFacade(OpeningHoursService openingHoursService) {
        this.openingHoursService = openingHoursService;
    }

    @Transactional
    public AddHoursResponseDto addOpeningHours(List<OpeningHoursDto> openingHours, Salon salon) {
        AddHoursResponseDto result = openingHoursService.addOpeningHours(openingHours, salon);

        if(result.message().equals("success")) {
            return result;
        }

        return new AddHoursResponseDto("failure", null);
    }
}
