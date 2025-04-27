package com.example.systemrezerwacji.domain.employeemodule.dto;

import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;

import java.util.List;

public record EmployeeWithAllInformationDto (Long employeeId, Long salonId, String name, String email, List<EmployeeAvailabilityDto> availability, List<OfferDto> offerList) {}
