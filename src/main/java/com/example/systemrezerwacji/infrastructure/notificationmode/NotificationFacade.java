package com.example.systemrezerwacji.infrastructure.notificationmode;

import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.reservationmodule.ReservationFacade;
import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationToTomorrow;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import com.example.systemrezerwacji.domain.salonmodule.dto.SalonWithIdDto;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import com.example.systemrezerwacji.domain.usermodule.dto.UserRegisterDto;
import com.example.systemrezerwacji.infrastructure.notificationmode.response.NotificationFacadeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Log4j2
public class NotificationFacade {
    private final EmailService emailService;
    private final ReservationFacade reservationFacade;
    private final SalonFacade salonFacade;
    private final UserFacade userFacade;
    private final OfferFacade offerFacade;

    public NotificationFacadeResponse sendAnEmailWhenClientHasAccount(String to, String offerName, LocalDateTime time,String companyName) {
        Boolean isSuccess = emailService.sendHtmlEmail(to, offerName, time,companyName);
        return new NotificationFacadeResponse(isSuccess);
    }

    public NotificationFacadeResponse sendAnEmailWhenClientDoNotHasAccount(String to, String offerName, String password,LocalDateTime time, String companyName) {
        Boolean isSuccess = emailService.sendHtmlEmailWithPassword(to, offerName, password,time,companyName);
        return new NotificationFacadeResponse(isSuccess);
    }

    public List<NotificationFacadeResponse> sendRemind() {
        List<ReservationToTomorrow> allReservations = reservationFacade.getAllReservationToTomorrow();
        log.info(allReservations.size() + " reservations to remind about");

        return allReservations.stream()
                .map(reservation -> {
                    Optional<SalonWithIdDto> salonOpt = salonFacade.getSalonById(reservation.salonId());
                    Optional<UserRegisterDto> userOpt = userFacade.getUserById(reservation.userId());
                    Offer offer = offerFacade.getOffer(reservation.offerId());

                    if (salonOpt.isPresent() && userOpt.isPresent()) {
                        SalonWithIdDto salon = salonOpt.get();
                        UserRegisterDto user = userOpt.get();

                        return remindUserToReservation(
                                user.email(),
                                offer.getName(),
                                salon,
                                reservation.reservationDataTime()
                        );
                    } else {
                        return new NotificationFacadeResponse(false);
                    }
                })
                .toList();
    }

    private NotificationFacadeResponse remindUserToReservation(String to, String offerName, SalonWithIdDto salon, LocalDateTime time ) {
        Boolean isSuccess = emailService.sendHtmlEmailToRemindAboutReservation(to,offerName,salon.salonName(),salon.city(),
                salon.street(),salon.number(),time);
        return new NotificationFacadeResponse(isSuccess);
    }

}
