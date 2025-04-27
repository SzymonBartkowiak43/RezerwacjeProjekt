package com.example.systemrezerwacji.infrastructure.emailSender.http.dto;

import java.time.LocalDateTime;

public record EmailRequestWithPasswordDto(String to,
                                          String offerName,
                                          LocalDateTime time,
                                          String company,
                                          String password) {
}
