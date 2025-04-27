package com.example.systemrezerwacji.infrastructure.loginandregister;

import com.example.systemrezerwacji.infrastructure.loginandregister.dto.JwtResponseDto;
import com.example.systemrezerwacji.infrastructure.loginandregister.dto.TokenRequestDto;
import com.example.systemrezerwacji.infrastructure.security.JwtAuthenticatorFacade;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://164.90.190.165")
public class TokenController {

    private final JwtAuthenticatorFacade jwtAuthenticatorFacade;

    @PostMapping("/token")
    public ResponseEntity<JwtResponseDto> authericateAndGenerateToken(@RequestBody TokenRequestDto tokenRequest) {
        final JwtResponseDto jwtResponse = jwtAuthenticatorFacade.authenticateAndGenerateToken(tokenRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
