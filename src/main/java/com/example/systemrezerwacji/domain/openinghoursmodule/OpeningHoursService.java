package com.example.systemrezerwacji.domain.openinghoursmodule;


import com.example.systemrezerwacji.infrastructure.dayofweek.DayOfWeek;
import com.example.systemrezerwacji.domain.openinghoursmodule.dto.OpeningHoursDto;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.salonmodule.dto.AddHoursResponseDto;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Service
class OpeningHoursService {
    private static final Logger log = LogManager.getLogger(OpeningHoursService.class);
    private final OpeningHoursRepository openingHoursRepository;

    OpeningHoursService(OpeningHoursRepository openingHoursRepository) {
        this.openingHoursRepository = openingHoursRepository;
        log.info("OpeningHoursService initialized");
    }


    AddHoursResponseDto addOpeningHours(List<OpeningHoursDto> openingHoursDto, Salon salon) {
        log.info("Adding opening hours for salon id: {}", salon.getId());
        openingHoursRepository.deleteBySalon(salon);
        log.debug("Existing opening hours deleted for salon id: {}", salon.getId());
        List<OpeningHours> openingHoursList = openingHoursDto.stream()
                .map(dto -> {
                            if(dto.openingTime().isAfter(dto.closingTime())) {
                                log.error("Opening time {} is after closing time {} for day {}",
                                        dto.openingTime(), dto.closingTime(), dto.dayOfWeek());
                                throw new IllegalArgumentException("Opening time cannot be after closing time");
                            }
                            OpeningHours openingHours = new OpeningHours();
                            openingHours.setDayOfWeek(DayOfWeek.valueOf(dto.dayOfWeek()));
                            openingHours.setOpeningTime(dto.openingTime());
                            openingHours.setClosingTime(dto.closingTime());
                            openingHours.setSalon(salon);
                    log.debug("Prepared opening hours for day {}: {} - {}",
                            dto.dayOfWeek(), dto.openingTime(), dto.closingTime());
                            return openingHours;
                        })
                .collect(Collectors.toList());

        Iterable<OpeningHours> openingHours = openingHoursRepository.saveAll(openingHoursList);
        log.info("Saved {} opening hours for salon id: {}", openingHoursList.size(), salon.getId());
        return new AddHoursResponseDto("success", openingHoursList);
    }


}
