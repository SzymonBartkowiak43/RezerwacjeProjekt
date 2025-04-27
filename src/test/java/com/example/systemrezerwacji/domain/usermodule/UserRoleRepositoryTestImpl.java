package com.example.systemrezerwacji.domain.usermodule;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRoleRepositoryTestImpl implements UserRoleRepository {

    private Map<Long, UserRole> userRolesDataBase = new ConcurrentHashMap<>();
    private Long id = 5L;

    UserRoleRepositoryTestImpl() {
        UserRole user = new UserRole("USER", "People who would like to make reservations");
        UserRole employee = new UserRole("EMPLOYEE", "Staff members who manage reservations and services");
        UserRole admin = new UserRole("ADMIN", "Administrators with full system access and management capabilities");
        UserRole owner = new UserRole("OWNER", "Salon owners with management access to their respective salons");
        save(user);
        save(employee);
        save(admin);
        save(owner);
    }

    @Override
    public Optional<UserRole> findByName(String name) {
        return userRolesDataBase.values().stream()
                .filter(userRole -> userRole.getName().equals(name))
                .findFirst();
    }

    @Override
    public <S extends UserRole> S save(S entity) {
        userRolesDataBase.put(id, entity);
        id++;
        return entity;
    }

    @Override
    public <S extends UserRole> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<UserRole> findById(Long aLong) {
        return userRolesDataBase.values().stream()
                .filter(userRole -> userRole.getId().equals(aLong))
                .findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<UserRole> findAll() {
        return null;
    }

    @Override
    public Iterable<UserRole> findAllById(Iterable<Long> longs) {
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
    public void delete(UserRole entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends UserRole> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
