package com.example.systemrezerwacji.domain.openinghoursmodule;


import com.example.systemrezerwacji.infrastructure.dayofweek.DayOfWeek;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class OpeningHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime openingTime;
    private LocalTime closingTime;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    public OpeningHours() {

    }

    void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    void setSalon(Salon salon) {
        this.salon = salon;
    }
}
