package com.example.systemrezerwacji.domain.offermodule;

import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;

class OfferMapper {
    static OfferDto toDto(Offer offer) {
        return new OfferDto(
                offer.getId(),
                offer.getName(),
                offer.getDescription(),
                offer.getPrice(),
                offer.getDuration()
        );
    }
}
