package com.example.systemrezerwacji.domain.usermodule.dto;

import com.example.systemrezerwacji.domain.usermodule.User;
import jakarta.annotation.Nullable;

public record UserCreatedWhenRegisteredDto(User user, Boolean isNewUser,@Nullable String unHashedPassword) {
}
