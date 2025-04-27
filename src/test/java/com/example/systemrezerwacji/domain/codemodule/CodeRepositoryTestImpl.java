package com.example.systemrezerwacji.domain.codemodule;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CodeRepositoryTestImpl implements CodeRepository {

    private Map<Long,Code> codeDataBase = new ConcurrentHashMap<>();
    private Long id = 1L;

    @Override
    public <S extends Code> S save(S entity) {
        codeDataBase.put(id,  entity);
        id++;
        return entity;
    }

    @Override
    public Optional<Code> findByCode(String code) {
        return codeDataBase.values().stream()
                .filter(c -> Objects.equals(c.getCode(), code))
                .findFirst();
    }

    @Override
    public <S extends Code> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Code> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Code> findAll() {
        return null;
    }

    @Override
    public Iterable<Code> findAllById(Iterable<Long> longs) {
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
    public void delete(Code entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Code> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
