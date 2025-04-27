package com.example.systemrezerwacji.domain.reservationmodule.response;

import com.example.systemrezerwacji.domain.employeemodule.Employee;
import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.usermodule.dto.UserCreatedWhenRegisteredDto;

public record ReservationEntities(
        Salon salon,
        Employee employee,
        Offer offer,
        UserCreatedWhenRegisteredDto userInfo
        ) {}
