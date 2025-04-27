package com.example.systemrezerwacji.domain.salonmodule;

import com.example.systemrezerwacji.domain.codemodule.CodeFacade;
import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.openinghoursmodule.OpeningHoursFacade;
import com.example.systemrezerwacji.domain.reservationmodule.ReservationFacade;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SalonConfiguration {

    @Mock
    SalonFacade salonFacade;

    @Mock
    CodeFacade codeFacade;

    @Mock
    UserFacade userFacade;

    @Mock
    OpeningHoursFacade openingHoursFacade;

    @Mock
    EmployeeFacade employeeFacade;

    @Mock
    OfferFacade offerFacade;

    @Mock
    ReservationFacade reservationFacade;

    public SalonConfiguration() {
        MockitoAnnotations.openMocks(this);
    }

    public SalonFacade createForTest(SalonRepository salonRepository, ImageRepository imageRepository) {
        MaperSalonToSalonWithIdDto mapper = new MaperSalonToSalonWithIdDto();
        SalonValidator salonValidator = new SalonValidator();
        SalonService salonService = new SalonService(salonRepository, mapper, imageRepository);
        SalonCreator salonCreator = new SalonCreator(salonValidator, userFacade, codeFacade, salonService);
        return new SalonFacade(userFacade, openingHoursFacade, employeeFacade, offerFacade, reservationFacade, salonCreator, salonService);
    }
}
