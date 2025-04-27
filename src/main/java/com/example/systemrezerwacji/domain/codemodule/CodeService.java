package com.example.systemrezerwacji.domain.codemodule;

import com.example.systemrezerwacji.domain.codemodule.dto.CodeDto;
import com.example.systemrezerwacji.domain.codemodule.message.ConsumeMessage;
import com.example.systemrezerwacji.domain.usermodule.User;
import org.springframework.stereotype.Service;

import java.util.UUID;
import static com.example.systemrezerwacji.domain.codemodule.CodeError.*;

@Service
class CodeService {
    private final CodeRepository codeRepository;

    CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    CodeDto generateNewCode() {
        String uniqueCode = UUID.randomUUID().toString();
        Code newCode = new Code(uniqueCode);

        Code save = codeRepository.save(newCode);

        return CodeMapper.toDto(save);
    }

    ConsumeMessage consumeCode(String codeValue, User user) {
        return codeRepository.findByCode(codeValue)
                .map(code -> {
                    if(!code.getIsConsumed()) {
                        code.setConsumed();
                        code.setUser(user);
                        codeRepository.save(code);
                        return ConsumeMessage.success();
                    } else {
                        return ConsumeMessage.failure(CODE_ALREADY_CONSUMED.getMessage());
                    }
                })
                .orElseGet(() -> ConsumeMessage.failure(CODE_NOT_FOUND.getMessage()));
    }

    public String getLinkToCode() {
        return "https://buy.stripe.com/test_bIYeXu5QW2za3D2aEF";
    }
}
