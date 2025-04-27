package com.example.systemrezerwacji.domain.usermodule;

import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeDto;
import com.example.systemrezerwacji.domain.usermodule.dto.UserCreatedWhenRegisteredDto;
import com.example.systemrezerwacji.domain.usermodule.dto.UserDto;
import com.example.systemrezerwacji.domain.usermodule.dto.UserRegisterDto;
import com.example.systemrezerwacji.domain.usermodule.response.UserFacadeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.*;

import static com.example.systemrezerwacji.domain.usermodule.UserValidationResult.SUCCESS_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;


class UserFacadeTest {

    UserRepository userRepository = new UserRepositoryTestImpl();
    UserRoleRepository userRoleRepository = new UserRoleRepositoryTestImpl();

    private UserFacade userFacade;

    private final Long testUserId = 1L;
    private final String testEmail = "test@example.com";
    private final String testName = "John";

    @BeforeEach
    void setUp() {
        UserConfiguration userConfiguration = new UserConfiguration();
        userFacade = userConfiguration.createForTest(userRepository, userRoleRepository);
    }


    @Test
    void shouldFindUserByEmailSuccessfully() {
        // given
        User user = new User();
        user.setEmail(testEmail);
        user.setId(testUserId);
        user.setRoles(Set.of(new UserRole("USER", "Description")));
        userRepository.save(user);

        // when
        UserDto result = userFacade.findByEmail(testEmail);

        // then
        assertThat(result.email()).isEqualTo(testEmail);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // given

        // when & then
        assertThatThrownBy(() -> userFacade.findByEmail(testEmail)).isInstanceOf(BadCredentialsException.class).hasMessageContaining("Not found");
    }

    @Test
    void shouldCreateNewUserSuccessfully() {
        // given
        UserRegisterDto dto = new UserRegisterDto(testEmail, testName, "Password123!");
        User user = new User();
        user.setId(1L);
        user.setEmail(testEmail);
        user.setId(testUserId);
        userRepository.save(user);

        // when
        UserFacadeResponse response = userFacade.createNewUser(dto);

        // then
        assertThat(response.message()).isEqualTo(SUCCESS_MESSAGE);
    }

    @Test
    void shouldReturnErrorWhenValidationFails() {
        // given
        UserRegisterDto invalidDto = new UserRegisterDto("", "invalid-email", "short");

        // when
        UserFacadeResponse response = userFacade.createNewUser(invalidDto);

        // then
        assertThat(response.message()).contains("Name contains forbidden characters");
        assertThat(response.userId()).isNull();

    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // given
        User user = new User();
        user.setId(testUserId);
        user.setEmail(testEmail);
        userRepository.save(user);

        // when
        Optional<UserRegisterDto> result = userFacade.getUserById(testUserId);

        // then
        assertThat(result.get().email()).isEqualTo(testEmail);
    }

    @Test
    void shouldAddOwnerRoleSuccessfully() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail(testEmail);
        user.setId(testUserId);
        Set<UserRole> roles = new HashSet<>();
        roles.add(new UserRole("USER", "Description"));
        user.setRoles(roles);

        userRepository.save(user);

        // when
        Optional<User> result = userFacade.addUserRoleOwner(testUserId);

        // then
        assertThat(result).isPresent().contains(user);
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        // given
        String employeeEmail = "employee@example.com";
        EmployeeDto employeeDto = new EmployeeDto(1L, "John", employeeEmail, List.of());

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail(employeeEmail);
        newUser.setName(employeeDto.name());

        // when
        Optional<User> result = userFacade.createEmployee(employeeDto);

        // then
        assertThat(result.get().getEmail()).isEqualTo(employeeEmail);
        assertThat(result.get().getRoles().size()).isEqualTo(2);
    }

    @Test
    void shouldGetExistingUserByEmail() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail(testEmail);
        user.setId(testUserId);
        userRepository.save(user);

        // when
        UserCreatedWhenRegisteredDto result = userFacade.getUserByEmailOrCreateNewAccount(testEmail);

        // then
        assertThat(result.user()).isEqualTo(user);
        assertThat(result.isNewUser()).isFalse();
    }

    @Test
    void shouldCreateNewUserWhenNotFoundByEmail() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail(testEmail);
        user.setId(testUserId);
        userRepository.save(user);

        // when
        UserCreatedWhenRegisteredDto result = userFacade.getUserByEmailOrCreateNewAccount(testEmail);

        // then
        assertThat(result.user().getEmail()).isEqualTo(testEmail);
        assertThat(result.isNewUser()).isFalse();
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // given
        UserRegisterDto dto = new UserRegisterDto( testEmail, "NewName","NewPassword123!");

        User user = new User();
        user.setId(1L);
        user.setEmail(testEmail);
        user.setId(testUserId);
        user.setName("Old Name");
        userRepository.save(user);


        // when
        UserFacadeResponse response = userFacade.updateUser(dto);

        // then
        assertThat(response.name()).isEqualTo("NewName");
        assertThat(response.userId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistingUser() {


        // when & then
        assertThatThrownBy(() -> userFacade.getUserByEmail(testEmail)).isInstanceOf(RuntimeException.class).hasMessageContaining("user not found!!");
    }
}

