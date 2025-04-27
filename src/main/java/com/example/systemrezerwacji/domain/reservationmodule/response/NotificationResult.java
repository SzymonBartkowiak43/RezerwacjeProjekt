package com.example.systemrezerwacji.domain.reservationmodule.response;

import com.example.systemrezerwacji.domain.usermodule.dto.UserCreatedWhenRegisteredDto;

public record NotificationResult(
        boolean success,
        UserCreatedWhenRegisteredDto userInfo
) {}
