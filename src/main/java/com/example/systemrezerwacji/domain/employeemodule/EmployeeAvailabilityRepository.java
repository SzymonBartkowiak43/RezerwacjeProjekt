package com.example.systemrezerwacji.domain.employeemodule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface EmployeeAvailabilityRepository extends CrudRepository<EmployeeAvailability, Long> {
    Optional<EmployeeAvailability> findByEmployeeIdAndDayOfWeek(Long employee_id, java.time.DayOfWeek dayOfWeek);
}
