package com.example.systemrezerwacji.domain.usermodule;

import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeDto;
import com.example.systemrezerwacji.domain.usermodule.dto.UserRegisterDto;
import com.example.systemrezerwacji.domain.usermodule.dto.UserCreatedWhenRegisteredDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);
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
        log.info("UserService initialized");
    }

    User createNewUser(UserRegisterDto userDto) {
        User user = createUser(userDto);
        log.info("Account created successfully with ID: {}", user.getId());
        return user;
    }

    Optional<UserRegisterDto> getUser(Long id) {
        log.debug("Fetching user DTO by id: {}", id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            log.info("User found with id: {}", id);
        } else {
            log.warn("User not found with id: {}", id);
        }
        return optionalUser.map(mapper::map);
    }

    Optional<User> getUserWithId(Long id) {
        log.debug("Fetching user entity by id: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            log.info("User entity found with id: {}", id);
        } else {
            log.warn("User entity not found with id: {}", id);
        }
        return userRepository.findById(id);
    }

    Optional<User> addRoleOwner(Long id) {
        log.info("Adding OWNER role to user with id: {}", id);
        UserRole userRole = userRoleRepository.findByName(OWNER_ROLE).orElseThrow(()-> {
            log.error("Owner role not found in repository");
            return new RuntimeException("Owner role not found");
        });
        Optional<User> userWithId = getUserWithId(id);

        if (userWithId.isPresent()) {
            User user = userWithId.get().addUserRole(userRole);
            userRepository.save(user);
            log.info("OWNER role added to user with id: {}", id);
        } else {
            log.warn("Cannot add OWNER role, user not found with id: {}", id);
        }

        return userWithId;
    }

    Optional<User> createEmployee(EmployeeDto employeeDto) {
        log.info("Creating employee for email: {}", employeeDto.email());
        String password = PasswordGenerator.generatePassword();
        UserRegisterDto userRegister = new UserRegisterDto(employeeDto.email(), employeeDto.name(), password);

        User newUser = createNewUser(userRegister);
        Optional<User> employee = addRoleEmployee(newUser.getId());
        if (employee.isPresent()) {
            log.info("Employee created with id: {}", newUser.getId());
        } else {
            log.warn("Failed to assign EMPLOYEE role to user with id: {}", newUser.getId());
        }

        return employee;
    }

    String getNameById(Long id) {
        log.debug("Getting name for user id: {}", id);
        User userById = userRepository.getUserById(id);
        log.info("User name retrieved for id {}: {}", id, userById.getName());
        return userById.getName();
    }

    Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        return userRepository.getUserByEmail(email);
    }

    UserCreatedWhenRegisteredDto getUserByEmailOrCreateNewAccount(String email) {
        log.info("Getting user by email or creating new account for: {}", email);
        Optional<User> userByEmail = userRepository.getUserByEmail(email);

        if(userByEmail.isPresent()) {
            log.info("User already exists for email: {}", email);
            return new UserCreatedWhenRegisteredDto(userByEmail.get(),false, null);
        }
        String password = PasswordGenerator.generatePassword();
        User newUser = createNewUserByEmail(email, password);

        return new UserCreatedWhenRegisteredDto(newUser, true, password);
    }

    private User createUser(UserRegisterDto userDto) {
        log.debug("Building user entity for email: {}", userDto.email());
        User user = new User.UserBuilder()
                .addName(userDto.name())
                .addEmail(userDto.email())
                .addPassword(userDto.password())
                .addUserRole(getDefaultRole())
                .build();

        log.info("User saved to repository with id: {}", user.getId());
        userRepository.save(user);
        return user;
    }

    private UserRole getDefaultRole() {
        log.debug("Fetching default user role: {}", DEFAULT_USER_ROLE);
        return userRoleRepository
                .findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() ->{log.error("User Role '{}' does not exist!", DEFAULT_USER_ROLE); return new BadCredentialsException("User Role not Exists!");});
    }

    private Optional<User> addRoleEmployee(Long id) {
        log.info("Adding EMPLOYEE role to user with id: {}", id);
        UserRole userRole = userRoleRepository.findByName(EMPLOYEE_ROLE).orElseThrow(() -> {
            log.error("Employee role not found in repository");
            return new RuntimeException("Employee role not found");
        });
        Optional<User> userWithId = getUserWithId(id);

        User user = userWithId.get().addUserRole(userRole);
        log.info("EMPLOYEE role added to user with id: {}", id);
        userRepository.save(user);

        return userWithId;
    }

    private User createNewUserByEmail(String email, String password) {
        log.debug("Creating new user by email: {}", email);
        String hashedPassword = passwordEncoder.encode(password);

        UserRegisterDto userRegisterDto = new UserRegisterDto(email, GUEST, hashedPassword);
        User user = createUser(userRegisterDto);
        return user;
    }


    public User updateUser(UserRegisterDto userDto) {
        log.info("Updating user with email: {}", userDto.email());
        User user = userRepository.getUserByEmail(userDto.email())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDto.email());
                    return new RuntimeException("user not found!!");
                });

        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());

        userRepository.save(user);
        return user;
    }
}
