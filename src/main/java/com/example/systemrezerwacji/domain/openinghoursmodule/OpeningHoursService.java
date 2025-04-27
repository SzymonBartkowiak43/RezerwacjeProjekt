package com.example.systemrezerwacji.domain.openinghoursmodule;


import com.example.systemrezerwacji.infrastructure.dayofweek.DayOfWeek;
import com.example.systemrezerwacji.domain.openinghoursmodule.dto.OpeningHoursDto;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.salonmodule.dto.AddHoursResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
class OpeningHoursService {
    private final OpeningHoursRepository openingHoursRepository;

    OpeningHoursService(OpeningHoursRepository openingHoursRepository) {
        this.openingHoursRepository = openingHoursRepository;
    }


    AddHoursResponseDto addOpeningHours(List<OpeningHoursDto> openingHoursDto, Salon salon) {

        openingHoursRepository.deleteBySalon(salon);
        List<OpeningHours> openingHoursList = openingHoursDto.stream()
                .map(dto -> {
                            if(dto.openingTime().isAfter(dto.closingTime())) {
                                throw new IllegalArgumentException("Opening time cannot be after closing time");
                            }
                            OpeningHours openingHours = new OpeningHours();
                            openingHours.setDayOfWeek(DayOfWeek.valueOf(dto.dayOfWeek()));
                            openingHours.setOpeningTime(dto.openingTime());
                            openingHours.setClosingTime(dto.closingTime());
                            openingHours.setSalon(salon);
                            return openingHours;
                        })
                .collect(Collectors.toList());

        Iterable<OpeningHours> openingHours = openingHoursRepository.saveAll(openingHoursList);

        return new AddHoursResponseDto("success", openingHoursList);
    }


}
