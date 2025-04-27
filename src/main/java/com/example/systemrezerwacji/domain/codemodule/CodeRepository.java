package com.example.systemrezerwacji.domain.codemodule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface CodeRepository extends CrudRepository<Code,Long> {
    Optional<Code> findByCode(String code);
}
