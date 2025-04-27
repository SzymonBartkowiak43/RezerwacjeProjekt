package com.example.systemrezerwacji.domain.employeemodule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
class EmployeeAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Getter
    private LocalTime startTime;
    @Getter
    private LocalTime endTime;

    @Getter
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
