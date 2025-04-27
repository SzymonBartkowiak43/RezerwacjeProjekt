package com.example.systemrezerwacji.domain.employeemodule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface EmployeeRepository extends CrudRepository<Employee, Long> {
    List<Employee> findByOffersId(Long offerId);

    List<Employee> findAllBySalonId(Long salonId);
}
