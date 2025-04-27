package com.example.systemrezerwacji.domain.reservationmodule;


import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationToTomorrow;
import com.example.systemrezerwacji.domain.reservationmodule.dto.UserReservationDataDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.UserReservationDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
class MapperReservationDto {

    UserReservationDto mapToUserReservationDto(Reservation reservation) {
        return new UserReservationDto(
                reservation.getId(),
                reservation.getSalon().getId(),
                reservation.getEmployee().getId(),
                reservation.getUser().getId(),
                reservation.getOffer().getId(),
                reservation.getReservationDateTime()
        );
    }


    List<UserReservationDto> mapToUserReservationDtoList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::mapToUserReservationDto)
                .collect(Collectors.toList());
    }

    UserReservationDataDto mapToUserReservationDataDto(Reservation reservation) {
        return new UserReservationDataDto(
                reservation.getId(),
                reservation.getSalon().getSalonName(),
                reservation.getEmployee().getUser().getName(),
                reservation.getOffer().getName(),
                reservation.getReservationDateTime()
        );
    }

    List<UserReservationDataDto> mapToUserReservationDataDtoList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::mapToUserReservationDataDto)
                .collect(Collectors.toList());
    }

    ReservationToTomorrow mapToReservationToTomorrow(Reservation reservation) {
        return new ReservationToTomorrow(
                reservation.getSalon().getId(),
                reservation.getUser().getId(),
                reservation.getOffer().getId(),
                reservation.getReservationDateTime()
        );
    }

    List<ReservationToTomorrow> mapToReservationToTomorrowList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::mapToReservationToTomorrow)
                .toList();
    }

    public Map<LocalDate, List<ReservationDto>> toMap(List<Reservation> reservationList) {
        Map<LocalDate, List<ReservationDto>> grouped = reservationList.stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getReservationDateTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.mapping(
                                reservation -> new ReservationDto(
                                        reservation.getId(),
                                        reservation.getEmployee().getUser().getName(),
                                        reservation.getOffer().getName(),
                                        reservation.getOffer().getPrice(),
                                        reservation.getReservationDateTime(),
                                        reservation.getReservationDateTime()
                                                .plusMinutes(reservation.getOffer().getDuration().getMinute())
                                ),
                                Collectors.toList()
                        )
                ));

        grouped.forEach((date, dtoList) ->
                dtoList.sort(Comparator.comparing(ReservationDto::reservationDateTimeStart))
        );

        return grouped;
    }
}