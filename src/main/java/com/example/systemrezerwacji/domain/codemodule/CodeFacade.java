package com.example.systemrezerwacji.domain.codemodule;

import com.example.systemrezerwacji.domain.codemodule.dto.CodeDto;
import com.example.systemrezerwacji.domain.codemodule.message.ConsumeMessage;
import com.example.systemrezerwacji.domain.usermodule.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class CodeFacade {
    private final CodeService codeService;

    @Transactional
    public CodeDto generateNewCode() {
        return codeService.generateNewCode();
    }

    @Transactional
    public ConsumeMessage consumeCode(String codeValue, User user) {
        return codeService.consumeCode(codeValue, user);
    }


    public String getLinkToCode() {
        return codeService.getLinkToCode();
    }
}
