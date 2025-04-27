package com.example.systemrezerwacji.domain.codemodule;

import com.example.systemrezerwacji.domain.codemodule.dto.CodeDto;
import com.example.systemrezerwacji.domain.codemodule.message.ConsumeMessage;
import com.example.systemrezerwacji.domain.usermodule.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.example.systemrezerwacji.domain.codemodule.CodeError.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CodeFacadeTest {

    CodeRepository codeRepository = new CodeRepositoryTestImpl();
    CodeFacade codeFacade =  new CodeConfiguration().createForTest(codeRepository);


    @Test
    void should_generate_and_save_code() {
        // given && when
        CodeDto codeDto = codeFacade.generateNewCode();

        //then
        assertThat(codeDto).isNotNull();
        assertThat(codeDto.code()).isNotNull();
        assertThat(codeRepository.findByCode(codeDto.code())).isNotEmpty();
    }

    @Test
    void should_consume_code_successfully_when_code_is_correct_and_not_consumed() {
        //given
        CodeDto code= codeFacade.generateNewCode();
        User user = new User();

        //when
        ConsumeMessage consumeMessage = codeFacade.consumeCode(code.code(), user);

        //then
        assertAll(
                () -> assertThat(consumeMessage.isSuccess()).isTrue(),
                () -> assertThat(consumeMessage.message()).isEqualTo("success")
        );

    }

    @Test
    void should_not_consume_code_when_code_is_correct_and_consumed() {
        //given
        CodeDto code = codeFacade.generateNewCode();
        User user = new User();

        //when
        ConsumeMessage consumeCode1 = codeFacade.consumeCode(code.code(), user);
        ConsumeMessage consumeCode2 = codeFacade.consumeCode(code.code(), user);
        Optional<Code> byCode = codeRepository.findByCode(code.code());

        //then
        assertAll(
                () -> assertThat(consumeCode1.isSuccess()).isTrue(),
                () -> assertThat(consumeCode2.isSuccess()).isFalse(),
                () -> assertThat(consumeCode2.message()).isEqualTo(CODE_ALREADY_CONSUMED.getMessage()),
                () -> assertThat(byCode.get().getIsConsumed()).isTrue()
        );
    }

    @Test
    void should_not_consume_code_when_not_found_code() {
        //given
        User user = new User();

        //when
        ConsumeMessage consumeMessage = codeFacade.consumeCode("notCorrectCode", user);

        //then
        assertThat(consumeMessage.isSuccess()).isFalse();
        assertThat(consumeMessage.message()).isEqualTo(CODE_NOT_FOUND.message);
    }

}