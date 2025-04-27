package com.example.systemrezerwacji.domain.openinghoursmodule;

import com.example.systemrezerwacji.domain.salonmodule.Salon;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OpeningHoursRepositoryTestImpl implements OpeningHoursRepository {

    private Map<Long, OpeningHours> openingHoursDataBase = new ConcurrentHashMap<>();
    private Long id = 1L;

    @Override
    public void deleteBySalon(Salon salon) {

    }

    @Override
    public <S extends OpeningHours> S save(S entity) {
        return null;
    }

    @Override
    public <S extends OpeningHours> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<OpeningHours> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<OpeningHours> findAll() {
        return null;
    }

    @Override
    public Iterable<OpeningHours> findAllById(Iterable<Long> longs) {
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
    public void delete(OpeningHours entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends OpeningHours> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
