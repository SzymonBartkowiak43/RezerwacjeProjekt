package com.example.systemrezerwacji.domain.salonmodule;

import com.example.systemrezerwacji.domain.codemodule.CodeFacade;
import com.example.systemrezerwacji.domain.codemodule.message.ConsumeMessage;
import com.example.systemrezerwacji.domain.salonmodule.dto.CreateNewSalonDto;
import com.example.systemrezerwacji.domain.salonmodule.exception.SalonCreationException;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
class SalonCreator {

    private final SalonValidator validator;
    private final UserFacade userFacade;
    private final CodeFacade codeFacade;
    private final SalonService salonService;

    public Long create(CreateNewSalonDto salonDto) throws SalonCreationException {
        validateSalonDto(salonDto);

        User user = findUserByEmail(salonDto.email());
        verifyCode(salonDto.code(), user);

        User owner = assignOwnerRole(user);
        return salonService.createNewSalon(salonDto, Optional.of(owner));
    }

    private void validateSalonDto(CreateNewSalonDto salonDto) throws SalonCreationException {
        String validationMessage = validator.validate(salonDto).validationMessage();
        if (!SalonValidationResult.SUCCESS_MESSAGE.equals(validationMessage)) {
            throw new SalonCreationException(validationMessage);
        }
    }

    private User findUserByEmail(String email) throws SalonCreationException {
        return userFacade.getUserByEmail(email);
    }

    private void verifyCode(String code, User user) throws SalonCreationException {
        ConsumeMessage consumeMessage = codeFacade.consumeCode(code, user);
        if (!consumeMessage.isSuccess()) {
            throw new SalonCreationException(consumeMessage.message());
        }
    }

    private User assignOwnerRole(User user) throws SalonCreationException {
        return userFacade.addUserRoleOwner(user.getId())
                .orElseThrow(() -> new SalonCreationException("Failed to assign owner role"));
    }

    private User findUser(String userId) throws SalonCreationException {
        return userFacade.getUserWithId(Long.valueOf(userId))
                .orElseThrow(() -> new SalonCreationException("User not found"));
    }

}
