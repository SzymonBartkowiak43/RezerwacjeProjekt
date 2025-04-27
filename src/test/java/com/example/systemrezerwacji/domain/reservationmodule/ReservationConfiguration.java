package com.example.systemrezerwacji.domain.reservationmodule;


import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import com.example.systemrezerwacji.infrastructure.notificationmode.NotificationFacade;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReservationConfiguration {

    @Mock
    UserFacade userFacade;

    @Mock
    OfferFacade offerFacade;

    @Mock
    SalonFacade salonFacade;

    @Mock
    EmployeeFacade employeeFacade;

    @Mock
    NotificationFacade notificationFacade;



    public ReservationConfiguration() {
        MockitoAnnotations.openMocks(this);
    }

    ReservationFacade createForTest(ReservationRepository reservationRepository) {
        MapperReservationDto mapperReservationDto = new MapperReservationDto();
        ReservationService reservationService = new ReservationService(reservationRepository, mapperReservationDto);
        ReservationValidator validator = new ReservationValidator(reservationService);
        ReservationResponseFactory reservationResponseFactory = new ReservationResponseFactory();
        return new ReservationFacade(offerFacade, userFacade, salonFacade, employeeFacade, notificationFacade, reservationService, validator,reservationResponseFactory);
    }
}
