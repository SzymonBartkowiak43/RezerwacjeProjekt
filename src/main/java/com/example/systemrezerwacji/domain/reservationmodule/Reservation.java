package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.employeemodule.Employee;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.usermodule.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @Setter
    private LocalDateTime reservationDateTime;

    Reservation(Salon salon, Employee employee, User user, Offer offer, LocalDateTime reservationDateTime) {
        this.salon = salon;
        this.employee = employee;
        this.user = user;
        this.offer = offer;
        this.reservationDateTime = reservationDateTime;
    }

    public Reservation() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }
}