package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.usermodule.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findAll();
    List<Reservation> findAllByUser(User user);
    List<Reservation> findAllByReservationDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Reservation> findAllBySalonId(Long salonId);
}



