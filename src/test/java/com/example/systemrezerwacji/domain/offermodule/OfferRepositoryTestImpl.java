package com.example.systemrezerwacji.domain.offermodule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OfferRepositoryTestImpl implements OfferRepository{

    private Map<Long, Offer> offerDataBase = new ConcurrentHashMap<>();
    private Long id = 1L;

    @Override
    public List<Offer> findAllBySalonId(Long salonId) {
        return offerDataBase.values().stream()
                .filter(offer -> offer.getSalon().getId().equals(salonId))
                .toList();
    }

    @Override
    public Optional<Offer> findOfferById(Long offerId) {
        return Optional.ofNullable(offerDataBase.get(offerId));
    }

    @Override
    public <S extends Offer> S save(S entity) {
        offerDataBase.put(id, entity);
        id++;
        return entity;
    }

    @Override
    public <S extends Offer> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Offer> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Offer> findAll() {
        return null;
    }

    @Override
    public Iterable<Offer> findAllById(Iterable<Long> longs) {
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
    public void delete(Offer entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Offer> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
