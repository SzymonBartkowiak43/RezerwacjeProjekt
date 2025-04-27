package com.example.systemrezerwacji.domain.salonmodule;

import com.example.systemrezerwacji.domain.usermodule.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SalonRepositoryTestImpl implements SalonRepository{

    private Map<Long, Salon> salonDataBase = new ConcurrentHashMap<>();
    private Long id = 1L;


    @Override
    public List<Salon> getSalonsByUser(User user) {
        return salonDataBase.values().stream()
                .filter(salon -> salon.getUser().equals(user))
                .toList();
    }

    @Override
    public <S extends Salon> S save(S entity) {
        salonDataBase.put(id, entity);
        id++;
        return entity;
    }

    @Override
    public <S extends Salon> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Salon> findById(Long aLong) {
        return Optional.ofNullable(salonDataBase.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Salon> findAll() {
        return salonDataBase.values().stream().toList();
    }

    @Override
    public Iterable<Salon> findAllById(Iterable<Long> longs) {
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
    public void delete(Salon entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Salon> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
