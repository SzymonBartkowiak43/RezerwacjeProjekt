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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
class ReservationService {
    private static final Logger log = LogManager.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final MapperReservationDto mapperReservationDto;


    ReservationService(ReservationRepository reservationRepository, MapperReservationDto mapperReservationDto) {
        this.reservationRepository = reservationRepository;
        this.mapperReservationDto = mapperReservationDto;
        log.info("ReservationService initialized");
    }

    List<AvailableTermDto> getEmployeeBusyTerms(Long employeeId, LocalDate date) {
        log.debug("Fetching busy terms for employeeId: {} on date: {}", employeeId, date);
        List<Reservation> allServicesOnSpecificDay = getReservations(employeeId, date);

        if (allServicesOnSpecificDay.isEmpty()) {
            log.info("No reservations found for employeeId: {} on date: {}", employeeId, date);
            return Collections.emptyList();
        }
        log.info("Found {} reservations for employeeId: {} on date: {}", allServicesOnSpecificDay.size(), employeeId, date);
        return getAvailableTermDto(allServicesOnSpecificDay);
    }

    void addNewReservation(Salon salon, Employee employee, User user, Offer offer, LocalDateTime reservationDateTime) {
        log.info("Adding new reservation for userId: {} with employeeId: {} at {}", user.getId(), employee.getId(), reservationDateTime);
        Reservation reservation = new Reservation(salon,employee,user,offer,reservationDateTime);
        log.info("Reservation saved for userId: {} at {}", user.getId(), reservationDateTime);
        reservationRepository.save(reservation);
    }

    private List<Reservation> getReservations(Long employeeId, LocalDate date) {
        return  reservationRepository.findAll().stream()
                .filter(employee -> employee.getEmployee().getId().equals(employeeId))
                .filter(data -> data.getReservationDateTime().toLocalDate().equals(date))
                .toList();

    }

    private List<AvailableTermDto> getAvailableTermDto(List<Reservation> allServicesOnSpecificDay) {
        log.debug("Getting reservations for employee");
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
        log.debug("Mapping reservations to AvailableTermDto");
        List<Reservation> allByUser = reservationRepository.findAllByUser(user);
        log.info("Mapped {} reservations to AvailableTermDto", allByUser.size());
        return mapperReservationDto.mapToUserReservationDataDtoList(allByUser);
    }

    public List<ReservationToTomorrow> getAllReservationToTomorrow() {
        log.info("Fetching reservations for tomorrow");
        LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1).minusSeconds(1);

        List<Reservation> allByReservationDateTimeBetween = reservationRepository.findAllByReservationDateTimeBetween(startOfTomorrow, endOfTomorrow);
        log.info("Found reservations for tomorrow");
        return mapperReservationDto.mapToReservationToTomorrowList(allByReservationDateTimeBetween);
    }

    public Boolean deleteReservation(Long reservationId, User userByEmail) {
        log.info("Attempting to delete reservationId: {} for userId: {}", reservationId, userByEmail.getId());
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.error("Reservation not found with id: {}", reservationId);
                    return new ReservationDeleteException("Reservation not found");
                });

        if(userByEmail == reservation.getUser()) {
            reservationRepository.delete(reservation);
            log.info("Reservation deleted with id: {} for userId: {}", reservationId, userByEmail.getId());
            return true;
        }
        log.warn("User id: {} is not the owner of reservation id: {}", userByEmail.getId(), reservationId);
        throw new ReservationDeleteException("This is not a reservation for this user");
    }

    public UserReservationDto updateReservationDate(Long reservationId, User userByEmail, LocalDateTime reservationDataTime) {
        log.info("Updating reservation date for reservationId: {} by userId: {}", reservationId, userByEmail.getId());
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.error("Reservation not found with id: {}", reservationId);
                    return new ReservationDeleteException("Reservation not found");
                });

        if(userByEmail == reservation.getUser()) {
            reservation.setReservationDateTime(reservationDataTime);
            Reservation savedReservation = reservationRepository.save(reservation);
            log.info("Reservation updated for id: {} to new date: {}", reservationId, reservationDataTime);
            return mapperReservationDto.mapToUserReservationDto(savedReservation);
        }
        log.warn("User id: {} is not the owner of reservation id: {}", userByEmail.getId(), reservationId);
        throw new ReservationDeleteException("This is not a reservation for this user");
    }

    public Reservation getReservation(Long reservationId) {
        log.debug("Fetching reservation by id: {}", reservationId);
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.error("Reservation not found with id: {}", reservationId);
                    return new ReservationDeleteException("Reservation not found");
                });
    }

    public Map<LocalDate, List<ReservationDto>> getAllReservationBySalonId(Long salonId) {
        log.info("Fetching all reservations for salonId: {}", salonId);
        List<Reservation> allBySalonId = reservationRepository.findAllBySalonId(salonId);
        log.info("Found {} reservations for salonId: {}", allBySalonId.size(), salonId);
        return mapperReservationDto.toMap(allBySalonId);
    }

}
