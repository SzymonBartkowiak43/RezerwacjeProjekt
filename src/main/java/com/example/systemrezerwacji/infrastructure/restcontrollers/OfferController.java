package com.example.systemrezerwacji.infrastructure.restcontrollers;

import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.offermodule.dto.CreateOfferDto;
import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.offermodule.response.OfferFacadeResponse;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import com.example.systemrezerwacji.domain.salonmodule.dto.SalonOffersListDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://164.90.190.165")
public class OfferController {

    private final OfferFacade offerFacade;
    private final SalonFacade salonFacade;

    public OfferController(OfferFacade offerFacade, SalonFacade salonFacade) {
        this.offerFacade = offerFacade;
        this.salonFacade = salonFacade;
    }

    @PostMapping("/offer")
    public ResponseEntity<OfferFacadeResponse> createNewOffer(@RequestBody CreateOfferDto createOfferDto) {
        OfferFacadeResponse newOffer = offerFacade.createOffer(createOfferDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newOffer.OfferId())
                .toUri();

        return ResponseEntity.created(location).body(newOffer);
    }

    @GetMapping("/offers/{salonId}")
    public ResponseEntity<List<OfferDto>> getOffers(@PathVariable Integer salonId) {
        SalonOffersListDto allOffers = salonFacade.getAllOffersToSalon(salonId.longValue());
        return ResponseEntity.ok(allOffers.offers());
    }



}
