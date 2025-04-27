package com.example.systemrezerwacji.domain.usermodule;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Service
public class UserRoleInitService {
    private final UserRoleRepository userRoleRepository;


    @Transactional
    public void init() {
        if (userRoleRepository.count() == 0) {
            List<UserRole> roles = Arrays.asList(
                    new UserRole("USER", "People who would like to make reservations"),
                    new UserRole("EMPLOYEE", "Staff members who manage reservations and services"),
                    new UserRole("ADMIN", "Administrators with full system access and management capabilities"),
                    new UserRole("OWNER", "Salon owners with management access to their respective salons")
            );


            userRoleRepository.saveAll(roles);
        }
    }

}
