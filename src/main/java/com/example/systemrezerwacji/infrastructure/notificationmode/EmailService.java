package com.example.systemrezerwacji.infrastructure.notificationmode;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Log4j2
class EmailService {

    private final SendMail sendMail;

    public Boolean sendHtmlEmail(String to, String offerName, LocalDateTime time, String companyName) {
        try {
            sendMail.sendEmail(to, offerName, time, companyName);
        } catch (Exception e) {
            System.err.println("Error while sending email: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Boolean sendHtmlEmailWithPassword(String to, String offerName, String password, LocalDateTime time, String companyName) {
        try {
            sendMail.sendEmailWithPassword(to,offerName,time,companyName, password);
        } catch (Exception e) {
            System.err.println("Error while sending email: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Boolean sendHtmlEmailToRemindAboutReservation(String to, String offerName, String salonName, String city, String street, String number, LocalDateTime time) {
        try {
            log.info("Sending email to remind about reservation");
            log.info("To: " + to);
            log.info("Offer name: " + offerName);
            log.info("Salon name: " + salonName);
            log.info("City: " + city);
            log.info("Street: " + street);
            log.info("Number: " + number);
            log.info("Time: " + time);
            sendMail.sendRemind(to, offerName, salonName, street, number, city, time);
        } catch (Exception e) {
            System.err.println("Error while sending email: " + e.getMessage());
            return false;
        }
        return true;
    }

}