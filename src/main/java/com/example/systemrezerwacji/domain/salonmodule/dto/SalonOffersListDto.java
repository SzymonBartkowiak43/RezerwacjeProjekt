package com.example.systemrezerwacji.domain.salonmodule.dto;

import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import jakarta.annotation.Nullable;

import java.util.List;

public record SalonOffersListDto(String message,@Nullable List<OfferDto> offers) {
}
