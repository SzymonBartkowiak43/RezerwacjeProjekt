package com.example.systemrezerwacji.domain.offermodule;

import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.offermodule.response.OfferFacadeResponse;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.offermodule.dto.CreateOfferDto;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferFacadeTest {

    OfferRepository offerRepository = new OfferRepositoryTestImpl();

    @Mock
    private SalonFacade salonFacade;

    private OfferFacade offerFacade;

    private final LocalTime testDuration = LocalTime.of(1, 30);
    private final Long testSalonId = 1L;
    private final Long testOfferId = 1L;

    @BeforeEach
    void setUp() {
        OfferConfiguration configuration = new OfferConfiguration();
        configuration.salonFacade = salonFacade;
        offerFacade = configuration.createForTest(offerRepository);
    }

    @Test
    void should_return_all_offers_for_given_salon() {
        // given
        Salon salon = new Salon();
        salon.setId(1L);

        Offer offer = new Offer("Test Offer", "Offer description", BigDecimal.valueOf(100.0), testDuration, salon);
        offer.setId(testOfferId);
        offerRepository.save(offer);

        // when
        List<OfferDto> result = offerFacade.getAllOffersToSalon(testSalonId);

        // then
        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(OfferDto::id, OfferDto::name)
                .containsExactly(testOfferId, "Test Offer");
    }

    @Test
    void should_return_correct_duration_for_offer() {
        // given
        Salon salon = new Salon();
        salon.setId(1L);

        Offer offer = new Offer("Test Offer", "Offer description", BigDecimal.valueOf(100.0), testDuration, salon);
        offer.setId(testOfferId);
        offerRepository.save(offer);

        // when
        LocalTime result = offerFacade.getDurationToOffer(testOfferId);

        // then
        assertThat(result).isEqualTo(testDuration);
    }

    @Test
    void should_retrieve_offer_by_id() {
        // given
        Offer expectedOffer = new Offer();
        expectedOffer.setId(testOfferId);
        offerRepository.save(expectedOffer);

        // when
        Offer result = offerFacade.getOffer(testOfferId);

        // then
        assertThat(result).isEqualTo(expectedOffer);
    }

    @Test
    void should_create_new_offer_successfully() {
        // given
        CreateOfferDto createDto = new CreateOfferDto(
                "New Offer", "Description", BigDecimal.valueOf(150.0),testDuration, Integer.valueOf(testSalonId.toString())
        );

        Salon testSalon = new Salon();
        testSalon.setId(testSalonId);

        when(salonFacade.getSalon(testSalonId)).thenReturn(testSalon);

        // when
        OfferFacadeResponse response = offerFacade.createOffer(createDto);

        // then
        assertThat(response.message()).isEqualTo("success");

    }

}