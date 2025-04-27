package com.example.systemrezerwacji;

import com.example.systemrezerwacji.domain.usermodule.UserRoleInitService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRoleInitService userRoleInitService;

    public DataLoader(UserRoleInitService userRoleInitService) {
        this.userRoleInitService = userRoleInitService;
    }

    @Override
    public void run(String... args) throws Exception {
        userRoleInitService.init();
    }
}