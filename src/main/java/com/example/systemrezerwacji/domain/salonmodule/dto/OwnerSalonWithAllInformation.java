package com.example.systemrezerwacji.domain.salonmodule.dto;

import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeWithAllInformationDto;
import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record OwnerSalonWithAllInformation(Map<LocalDate, List<ReservationDto>> reservationDto, List<EmployeeWithAllInformationDto> employeeDto, List<OfferDto> offerDto, String salonName) {
}
