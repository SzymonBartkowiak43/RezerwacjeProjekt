package com.example.systemrezerwacji.domain.codemodule.dto;

import java.time.LocalDateTime;

public record CodeDto(String code, boolean isConsumed, LocalDateTime dataGenerated, Long userId) {
}
