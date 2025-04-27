package com.example.systemrezerwacji.domain.salonmodule;


import com.example.systemrezerwacji.domain.openinghoursmodule.OpeningHours;
import com.example.systemrezerwacji.domain.usermodule.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Entity
@Getter
public class Salon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    @Setter
    private String salonName;
    private String category;
    private String city;
    private String zipCode;
    private String street;
    private String number;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpeningHours> openingHours;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    private Salon(SalonBuilder salonServiceBuilder) {
        this.salonName = salonServiceBuilder.name;
        this.category = salonServiceBuilder.category;
        this.city = salonServiceBuilder.city;
        this.zipCode = salonServiceBuilder.zipCode;
        this.street = salonServiceBuilder.street;
        this.number = salonServiceBuilder.number;
        this.user = salonServiceBuilder.user;
    }

    public Salon() {
        images = new ArrayList<>();
    }

    Long getUserid() {
        return user.getId();
    }

    void addOpeningHours(List<OpeningHours> openingHoursList) {
        openingHours = openingHoursList;
    }


    static class SalonBuilder {
        private String name;
        private String category;
        private String city;
        private String zipCode;
        private String street;
        private String number;
        private User user;

        SalonBuilder addName(String name) {
            this.name = name;
            return this;
        }

        SalonBuilder addCategory(String category) {
            this.category = category;
            return this;
        }

        SalonBuilder addCity(String city) {
            this.city = city;
            return this;
        }

        SalonBuilder addZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        SalonBuilder addStreet(String street) {
            this.street = street;
            return this;
        }

        SalonBuilder addNumber(String number) {
            this.number = number;
            return this;
        }

        SalonBuilder addUser(Optional<User> user) {
            this.user = user.orElseThrow(() -> new IllegalArgumentException("User must be present"));
            return this;
        }

        Salon build() {
            return new Salon(this);
        }

    }
}