package com.example.systemrezerwacji.domain.employeemodule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

public class EmployeeRepositoryTestImpl implements EmployeeRepository {

    private Map<Long, Employee> employeeDataBase = new ConcurrentHashMap<>();
    private Long id = 1L;

    @Override
    public List<Employee> findByOffersId(Long offerId) {
        return employeeDataBase.values().stream()
                .filter(employee -> employee.getOffers().stream()
                        .anyMatch(offer -> offer.getId().equals(offerId)))
                .toList();
    }

    @Override
    public List<Employee> findAllBySalonId(Long salonId) {
        return employeeDataBase.values().stream()
                .filter(employee -> employee.getSalon().getId().equals(salonId))
                .toList();
    }

    @Override
    public <S extends Employee> S save(S entity) {
        employeeDataBase.put(id, entity);
        id++;
        return entity;
    }

    @Override
    public <S extends Employee> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Employee> findById(Long aLong) {
        return Optional.ofNullable(employeeDataBase.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Employee> findAll() {
        return employeeDataBase.values();
    }

    @Override
    public Iterable<Employee> findAllById(Iterable<Long> longs) {
        return employeeDataBase.values().stream()
                .filter(employee -> StreamSupport.stream(longs.spliterator(), false)
                        .anyMatch(id -> id.equals(employee.getId())))
                .toList();
    }

    @Override
    public long count() {
        return id-1;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Employee entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Employee> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
