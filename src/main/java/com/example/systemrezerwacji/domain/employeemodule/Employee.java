package com.example.systemrezerwacji.domain.employeemodule;


import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeAvailability> availability = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "employee_offers",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "offer_id")
    )
    private List<Offer> offers = new ArrayList<>();

    public Employee() {

    }

    void setSalonAndUser(Salon salon, User user) {
        this.salon = salon;
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    void setAvailability(List<EmployeeAvailability> availability) {
        this.availability = availability;
    }

    void addOffer(Offer offer) {
        this.offers.add(offer);
    }


}
