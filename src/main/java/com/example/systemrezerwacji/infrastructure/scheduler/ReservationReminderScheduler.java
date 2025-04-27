package com.example.systemrezerwacji.infrastructure.scheduler;


import com.example.systemrezerwacji.infrastructure.notificationmode.NotificationFacade;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class ReservationReminderScheduler {

    private final NotificationFacade notificationFacade;

    @Scheduled(cron = "${reservation.send-remind}")
    public void sendRemindToUserWhoHaveReservationToTomorrow() {
        log.info("Start sending remind to service");
        notificationFacade.sendRemind();
    }
}
