package com.example.systemrezerwacji.domain.usermodule;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryTestImpl implements UserRepository {

    private Map<Long, User> usersDataBase = new ConcurrentHashMap<>();
    private Long id = 1L;

    @Override
    public User getUserById(Long id) {
        return usersDataBase.get(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return usersDataBase.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public <S extends User> S save(S entity) {
        if(entity.getId() == null) {
            entity.setId(id);
        }
        usersDataBase.put(id, entity);
        id++;
        return entity;
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return Optional.ofNullable(usersDataBase.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> longs) {
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
    public void delete(User entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
