package com.example.systemrezerwacji.domain.codemodule;

import com.example.systemrezerwacji.domain.usermodule.User;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private Boolean isConsumed;
    private LocalDateTime dataGenerated;
    private LocalDateTime dataConsumption;

    @OneToOne
    private User user;

    Code(String code) {
        this.code = code;
        this.isConsumed = false;
        this.dataGenerated = LocalDateTime.now();
    }
    public Code() {

    }

    void setConsumed() {
        this.isConsumed = true;
        this.dataConsumption = LocalDateTime.now();
    }


    void setUser(User user) {
        this.user = user;
    }
}
