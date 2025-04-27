package com.example.systemrezerwacji.domain.salonmodule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findBySalonId(Long salonId);
}

