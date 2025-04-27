package com.example.systemrezerwacji.domain.employeemodule;


import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;


@Service
class EmployeeAvailabilityService {
    private final EmployeeAvailabilityRepository employeeAvailabilityRepository;

    EmployeeAvailabilityService(EmployeeAvailabilityRepository employeeAvailabilityRepository) {
        this.employeeAvailabilityRepository = employeeAvailabilityRepository;
    }


    Iterable<EmployeeAvailability> saveAvability(List<EmployeeAvailability> employeeAvailabilityList) {
        return employeeAvailabilityRepository.saveAll(employeeAvailabilityList);
    }


    EmployeeAvailability findEmployeeAvability(Long employeeId, DayOfWeek dayOfWeek) {
        Optional<EmployeeAvailability> byEmployeeIdAndDayOfWeek = employeeAvailabilityRepository.findByEmployeeIdAndDayOfWeek(employeeId, dayOfWeek);
        return byEmployeeIdAndDayOfWeek.get();
    }

}
