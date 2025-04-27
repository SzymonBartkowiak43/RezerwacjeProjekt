package com.example.systemrezerwacji.domain.usermodule;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UserConfiguration {


    UserFacade createForTest(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        UserValidator userValidator = new UserValidator();
        MaperUserToUserRegisterDto mapper = new MaperUserToUserRegisterDto();
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return null;
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return false;
            }
        };
        UserService userService = new UserService(userRepository,userRoleRepository, mapper, passwordEncoder);
        return new UserFacade(userService, userValidator);
    }

}
