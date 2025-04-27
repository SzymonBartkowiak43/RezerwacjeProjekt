package com.example.systemrezerwacji.domain.offermodule;

import com.example.systemrezerwacji.domain.salonmodule.Salon;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalTime duration;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    public Offer() {

    }

    Offer(String name, String description, BigDecimal price, LocalTime duration, Salon salon) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.salon = salon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }
}
