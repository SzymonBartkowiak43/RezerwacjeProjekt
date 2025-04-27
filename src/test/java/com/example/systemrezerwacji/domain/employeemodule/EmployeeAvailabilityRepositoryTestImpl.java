package com.example.systemrezerwacji.domain.employeemodule;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeeAvailabilityRepositoryTestImpl implements EmployeeAvailabilityRepository{

    private Map<Long, EmployeeAvailability> employeeAvailabilityDataBase = new ConcurrentHashMap<>();
    private Long id = 1L;

    @Override
    public Optional<EmployeeAvailability> findByEmployeeIdAndDayOfWeek(Long employee_id, DayOfWeek dayOfWeek) {
        return employeeAvailabilityDataBase.values().stream()
                .filter(employeeAvailability -> employeeAvailability.getEmployee().getId().equals(employee_id))
                .filter(employeeAvailability -> employeeAvailability.getDayOfWeek().equals(dayOfWeek))
                .findFirst();
    }

    @Override
    public <S extends EmployeeAvailability> S save(S entity) {
        employeeAvailabilityDataBase.put(id, entity);
        id++;
        return entity;
    }

    @Override
    public <S extends EmployeeAvailability> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<EmployeeAvailability> findById(Long aLong) {
        return Optional.ofNullable(employeeAvailabilityDataBase.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<EmployeeAvailability> findAll() {
        return null;
    }

    @Override
    public Iterable<EmployeeAvailability> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(EmployeeAvailability entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends EmployeeAvailability> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
