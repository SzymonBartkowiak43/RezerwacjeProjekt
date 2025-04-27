package com.example.systemrezerwacji.infrastructure.restcontrollers;


import com.example.systemrezerwacji.domain.codemodule.CodeFacade;
import com.example.systemrezerwacji.domain.codemodule.dto.CodeDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://164.90.190.165")
@RequestMapping("/reservation-service/code")
public class CodeController {
    private final CodeFacade codeFacade;

    @PostMapping("/generateCode")
    public ResponseEntity<CodeDto> generateCode() {
        CodeDto codeDto = codeFacade.generateNewCode();
        return ResponseEntity.status(HttpStatus.CREATED).body(codeDto);
    }

    @GetMapping("/get-link-to-code")
    public ResponseEntity<String> getLinkToCode() {
        String link = codeFacade.getLinkToCode();
        return ResponseEntity.ok(link);
    }

}
