package com.example.systemrezerwacji.domain.offermodule;

import com.example.systemrezerwacji.domain.offermodule.dto.CreateOfferDto;
import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.offermodule.exception.OfferNotFoundException;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
class OfferService {
    private final OfferRepository offerRepository;

    OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    List<OfferDto> getAllOffers(Long salonId) {
        List<Offer> allOffers= offerRepository.findAllBySalonId(salonId);

        return allOffers.stream()
                .map(OfferMapper::toDto)
                .toList();
    }

    LocalTime getDurationByOfferId(Long offerId) {
        Offer offerById = offerRepository.findOfferById(offerId)
                .orElseThrow(() -> new OfferNotFoundException("Not found offer with id: " + offerId));

        return offerById.getDuration();
    }

    Offer getOffer(Long offerId) {
        return offerRepository.findOfferById(offerId)
                .orElseThrow(() -> new OfferNotFoundException("Not found offer with id: " + offerId));
    }

    public Offer createOffer(CreateOfferDto offerDto, Salon salon) {
        Offer offer = new Offer(offerDto.name(),offerDto.description(),offerDto.price(),offerDto.duration(),salon);
        return offerRepository.save(offer);
    }
}
