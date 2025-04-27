package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.employeemodule.Employee;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationToTomorrow;
import com.example.systemrezerwacji.domain.reservationmodule.dto.UserReservationDataDto;
import com.example.systemrezerwacji.domain.reservationmodule.exception.ReservationDeleteException;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.reservationmodule.dto.UserReservationDto;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MapperReservationDto mapperReservationDto;


    ReservationService(ReservationRepository reservationRepository, MapperReservationDto mapperReservationDto) {
        this.reservationRepository = reservationRepository;
        this.mapperReservationDto = mapperReservationDto;
    }

    List<AvailableTermDto> getEmployeeBusyTerms(Long employeeId, LocalDate date) {
        List<Reservation> allServicesOnSpecificDay = getReservations(employeeId, date);

        if (allServicesOnSpecificDay.isEmpty()) {
            return Collections.emptyList();
        }
        return getAvailableTermDto(allServicesOnSpecificDay);
    }

    void addNewReservation(Salon salon, Employee employee, User user, Offer offer, LocalDateTime reservationDateTime) {
        Reservation reservation = new Reservation(salon,employee,user,offer,reservationDateTime);

        reservationRepository.save(reservation);
    }

    private List<Reservation> getReservations(Long employeeId, LocalDate date) {
        return  reservationRepository.findAll().stream()
                .filter(employee -> employee.getEmployee().getId().equals(employeeId))
                .filter(data -> data.getReservationDateTime().toLocalDate().equals(date))
                .toList();

    }

    private List<AvailableTermDto> getAvailableTermDto(List<Reservation> allServicesOnSpecificDay) {
        List<AvailableTermDto> list = allServicesOnSpecificDay.stream()
                .map(reservation -> {
                    LocalTime start = reservation.getReservationDateTime().toLocalTime();
                    LocalTime end = start.plusMinutes(reservation.getOffer().getDuration().getMinute());
                    return new AvailableTermDto(start, end);
                })
                .toList();
        return list;
    }


    public List<UserReservationDataDto> getReservationToCurrentUser(User user) {
        List<Reservation> allByUser = reservationRepository.findAllByUser(user);
        return mapperReservationDto.mapToUserReservationDataDtoList(allByUser);
    }

    public List<ReservationToTomorrow> getAllReservationToTomorrow() {
        LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1).minusSeconds(1);

        List<Reservation> allByReservationDateTimeBetween = reservationRepository.findAllByReservationDateTimeBetween(startOfTomorrow, endOfTomorrow);
        return mapperReservationDto.mapToReservationToTomorrowList(allByReservationDateTimeBetween);
    }

    public Boolean deleteReservation(Long reservationId, User userByEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationDeleteException("Reservation not found"));

        if(userByEmail == reservation.getUser()) {
            reservationRepository.delete(reservation);
            return true;
        }
        throw new ReservationDeleteException("This is not a reservation for this user");
    }

    public UserReservationDto updateReservationDate(Long reservationId, User userByEmail, LocalDateTime reservationDataTime) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationDeleteException("Reservation not found"));

        if(userByEmail == reservation.getUser()) {
            reservation.setReservationDateTime(reservationDataTime);
            Reservation savedReservation = reservationRepository.save(reservation);
            return mapperReservationDto.mapToUserReservationDto(savedReservation);
        }
        throw new ReservationDeleteException("This is not a reservation for this user");
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationDeleteException("Reservation not found"));
    }

    public Map<LocalDate, List<ReservationDto>> getAllReservationBySalonId(Long salonId) {
        List<Reservation> allBySalonId = reservationRepository.findAllBySalonId(salonId);
        return mapperReservationDto.toMap(allBySalonId);
    }

}
