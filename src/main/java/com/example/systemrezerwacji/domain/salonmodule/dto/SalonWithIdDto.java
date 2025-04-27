package com.example.systemrezerwacji.domain.salonmodule.dto;

import java.io.Serializable;

public record SalonWithIdDto(String id,
                             String salonName,
                             String category,
                             String city,
                             String zipCode,
                             String street,
                             String number,
                             String userId) implements Serializable {
}
