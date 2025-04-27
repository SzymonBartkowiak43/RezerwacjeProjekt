package com.example.systemrezerwacji.domain.offermodule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface OfferRepository extends CrudRepository<Offer, Long> {
    List<Offer> findAllBySalonId(Long salonId);
    Optional<Offer> findOfferById(Long offerId);
}
