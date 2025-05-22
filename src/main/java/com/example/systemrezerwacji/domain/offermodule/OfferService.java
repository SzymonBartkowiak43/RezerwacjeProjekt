package com.example.systemrezerwacji.domain.offermodule;

import com.example.systemrezerwacji.domain.offermodule.dto.CreateOfferDto;
import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.offermodule.exception.OfferNotFoundException;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.util.List;

@Service
class OfferService {
    private static final Logger log = LogManager.getLogger(OfferService.class);
    private final OfferRepository offerRepository;

    OfferService(OfferRepository offerRepository) {
        log.info("OfferService initialized");
        this.offerRepository = offerRepository;
    }

    List<OfferDto> getAllOffers(Long salonId) {
        log.debug("Fetching all offers for salonId: {}", salonId);
        List<Offer> allOffers= offerRepository.findAllBySalonId(salonId);
        log.info("Found {} offers for salonId: {}", allOffers.size(), salonId);
        return allOffers.stream()
                .map(OfferMapper::toDto)
                .toList();
    }

    LocalTime getDurationByOfferId(Long offerId) {
        log.debug("Getting duration for offerId: {}", offerId);
        Offer offerById = offerRepository.findOfferById(offerId)
                .orElseThrow(() -> {
                    log.error("Offer not found with id: {}", offerId);
                    return new OfferNotFoundException("Not found offer with id: " + offerId);
                });
        return offerById.getDuration();
    }

    Offer getOffer(Long offerId) {
        log.debug("Fetching offer entity by id: {}", offerId);
        return offerRepository.findOfferById(offerId)
                .orElseThrow(() -> {
                    log.error("Offer not found with id: {}", offerId);
                    return new OfferNotFoundException("Not found offer with id: " + offerId);
                });
    }

    public Offer createOffer(CreateOfferDto offerDto, Salon salon) {
        log.info("Creating new offer '{}' for salonId: {}", offerDto.name(), salon.getId());
        Offer offer = new Offer(offerDto.name(),offerDto.description(),offerDto.price(),offerDto.duration(),salon);
        return offerRepository.save(offer);
    }
}
