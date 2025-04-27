package com.example.systemrezerwacji.domain.openinghoursmodule;


import com.example.systemrezerwacji.domain.salonmodule.Salon;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface OpeningHoursRepository extends CrudRepository<OpeningHours, Long> {
    void deleteBySalon(Salon salon);
}
