package com.example.systemrezerwacji.domain.codemodule;

import com.example.systemrezerwacji.domain.codemodule.message.ConsumeMessage;
import com.example.systemrezerwacji.domain.codemodule.dto.CodeDto;
import com.example.systemrezerwacji.domain.usermodule.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.example.systemrezerwacji.domain.codemodule.CodeError.CODE_ALREADY_CONSUMED;
import static com.example.systemrezerwacji.domain.codemodule.CodeError.CODE_NOT_FOUND;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


class CodeServiceTest {

    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private CodeService codeService;

    CodeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_generate_and_save_code() {
        //given
        when(codeRepository.save(any(Code.class))).thenReturn(new Code("code"));

        //when
        CodeDto codeDto = codeService.generateNewCode();

        //then
        assertThat(codeDto).isNotNull();
        assertThat(codeDto.code()).isNotNull();
        verify(codeRepository, times(1)).save(any(Code.class));
    }

    @Test
    void should_consume_code_successfully_when_code_is_correct_and_not_consumed() {
        //given
        Code code = new Code("code");
        User user = new User();
        when(codeRepository.findByCode(any())).thenReturn(Optional.of(code));

        //when
        ConsumeMessage consumeMessage = codeService.consumeCode(code.getCode(), user);


        //then
        assertThat(consumeMessage.isSuccess()).isTrue();
        assertThat(consumeMessage.message()).isEqualTo("success");
        assertThat(code.getIsConsumed()).isTrue();

    }

    @Test
    void should_not_consume_code_when_code_is_correct_and_consumed() {
        //given
        Code code = new Code("code");
        code.setConsumed();
        User user = new User();
        when(codeRepository.findByCode(any())).thenReturn(Optional.of(code));

        //when
        ConsumeMessage consumeMessage = codeService.consumeCode(code.getCode(), user);

        //then
        assertThat(code.getIsConsumed()).isTrue();
        assertThat(consumeMessage.isSuccess()).isFalse();
        assertThat(consumeMessage.message()).isEqualTo(CODE_ALREADY_CONSUMED.getMessage());
    }

    @Test
    void should_not_consume_code_when_not_found_code() {
        //given
        User user = new User();
        when(codeRepository.findByCode(any())).thenReturn(Optional.empty());

        //when
        ConsumeMessage consumeMessage = codeService.consumeCode("notCorrectCode", user);

        //then
        assertThat(consumeMessage.isSuccess()).isFalse();
        assertThat(consumeMessage.message()).isEqualTo(CODE_NOT_FOUND.message);
    }

}