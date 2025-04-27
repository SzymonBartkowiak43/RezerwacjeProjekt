package com.example.systemrezerwacji.domain.usermodule;

import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeDto;
import com.example.systemrezerwacji.domain.usermodule.dto.UserRegisterDto;
import com.example.systemrezerwacji.domain.usermodule.dto.UserCreatedWhenRegisteredDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserService {

    private static final String DEFAULT_USER_ROLE = "USER";
    private static final String OWNER_ROLE = "OWNER";
    private static final String EMPLOYEE_ROLE = "EMPLOYEE";
    private static final String ADMIN_ROLE = "ADMIN";
    public static final String GUEST = "Guest";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final MaperUserToUserRegisterDto mapper;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, MaperUserToUserRegisterDto mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    User createNewUser(UserRegisterDto userDto) {
        User user = createUser(userDto);
        return user;
    }

    Optional<UserRegisterDto> getUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        return optionalUser.map(mapper::map);
    }

    Optional<User> getUserWithId(Long id) {
        return userRepository.findById(id);
    }

    Optional<User> addRoleOwner(Long id) {
        UserRole userRole = userRoleRepository.findByName(OWNER_ROLE).orElseThrow();
        Optional<User> userWithId = getUserWithId(id);

        User user = userWithId.get().addUserRole(userRole);
        userRepository.save(user);

        return userWithId;
    }

    Optional<User> createEmployee(EmployeeDto employeeDto) {
        String password = PasswordGenerator.generatePassword();
        UserRegisterDto userRegister = new UserRegisterDto(employeeDto.email(), employeeDto.name(), password);

        User newUser = createNewUser(userRegister);
        Optional<User> employee = addRoleEmployee(newUser.getId());

        return employee;
    }

    String getNameById(Long id) {
        User userById = userRepository.getUserById(id);
        return userById.getName();
    }

    Optional<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    UserCreatedWhenRegisteredDto getUserByEmailOrCreateNewAccount(String email) {
        Optional<User> userByEmail = userRepository.getUserByEmail(email);

        if(userByEmail.isPresent()) {
            return new UserCreatedWhenRegisteredDto(userByEmail.get(),false, null);
        }
        String password = PasswordGenerator.generatePassword();
        User newUser = createNewUserByEmail(email, password);

        return new UserCreatedWhenRegisteredDto(newUser, true, password);
    }

    private User createUser(UserRegisterDto userDto) {
        User user = new User.UserBuilder()
                .addName(userDto.name())
                .addEmail(userDto.email())
                .addPassword(userDto.password())
                .addUserRole(getDefaultRole())
                .build();

        userRepository.save(user);
        return user;
    }

    private UserRole getDefaultRole() {
        return userRoleRepository
                .findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> new BadCredentialsException("User Role not Exists!"));
    }

    private Optional<User> addRoleEmployee(Long id) {
        UserRole userRole = userRoleRepository.findByName(EMPLOYEE_ROLE).orElseThrow();
        Optional<User> userWithId = getUserWithId(id);

        User user = userWithId.get().addUserRole(userRole);
        userRepository.save(user);

        return userWithId;
    }

    private User createNewUserByEmail(String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        UserRegisterDto userRegisterDto = new UserRegisterDto(email, GUEST, hashedPassword);
        User user = createUser(userRegisterDto);
        return user;
    }


    public User updateUser(UserRegisterDto userDto) {
        User user = userRepository.getUserByEmail(userDto.email())
                .orElseThrow(() -> new RuntimeException("user not found!!"));

        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());

        userRepository.save(user);
        return user;
    }
}
