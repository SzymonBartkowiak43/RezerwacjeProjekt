package com.example.systemrezerwacji.infrastructure.notificationmode;

import java.time.LocalDateTime;

public interface SendMail {
    Boolean sendEmail(String to, String offerName, LocalDateTime time, String company);
    Boolean sendEmailWithPassword(String to, String offerName, LocalDateTime time, String company, String password);
    void sendRemind(String to, String reservationName,String salonName, String street, String number, String city, LocalDateTime time);
}
