package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.employeemodule.Employee;
import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermWithDateDto;
import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.reservationmodule.dto.*;
import com.example.systemrezerwacji.domain.reservationmodule.exception.ReservationDeleteException;
import com.example.systemrezerwacji.domain.reservationmodule.response.ReservationFacadeResponse;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import com.example.systemrezerwacji.domain.usermodule.dto.UserCreatedWhenRegisteredDto;
import com.example.systemrezerwacji.infrastructure.notificationmode.NotificationFacade;
import com.example.systemrezerwacji.infrastructure.notificationmode.response.NotificationFacadeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationFacadeTest {

    ReservationRepository reservationRepository = new ReservationRepositoryTestImpl();

    @Mock
    private UserFacade userFacade;

    @Mock
    private OfferFacade offerFacade;

    @Mock
    private SalonFacade salonFacade;

    @Mock
    private EmployeeFacade employeeFacade;

    @Mock
    private NotificationFacade notificationFacade;

    ReservationFacade reservationFacade;

    LocalDateTime validDateTime = LocalDateTime.of(2026,2,2,12, 0);
    LocalDateTime invalidDateTime = LocalDateTime.of(2024,2,2,12, 0);


    @BeforeEach
    void setUp() {
        ReservationConfiguration configuration = new ReservationConfiguration();
        configuration.userFacade = userFacade;
        configuration.offerFacade = offerFacade;
        configuration.salonFacade = salonFacade;
        configuration.employeeFacade = employeeFacade;
        configuration.notificationFacade = notificationFacade;
        reservationFacade = configuration.createForTest(reservationRepository);
    }

    @Test
    void shouldReturnErrorWhenValidationFails() {
        // given
        CreateReservationDto invalidDto = new CreateReservationDto(
                1L, 1L, 1L, invalidDateTime,"test@example.com"
        );

        when(offerFacade.getDurationToOffer(1L)).thenReturn(LocalTime.of(1, 0));

        // when
        ReservationFacadeResponse response = reservationFacade.createNewReservation(invalidDto);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.message()).isNotNull();
    }

    @Test
    void shouldCreateReservationWhenAllConditionsMet() {
        // given
        CreateReservationDto validDto = new CreateReservationDto(
                1L, 1L, 1L, validDateTime, "test@example.com"
        );

        Salon testSalon = new Salon();
        Employee testEmployee = new Employee();
        Offer testOffer = new Offer();
        User testUser = new User();

        when(offerFacade.getDurationToOffer(1L)).thenReturn(LocalTime.of(1, 0));
        when(salonFacade.getSalon(1L)).thenReturn(testSalon);
        when(employeeFacade.getEmployee(1L)).thenReturn(testEmployee);
        when(offerFacade.getOffer(1L)).thenReturn(testOffer);
        when(userFacade.getUserByEmailOrCreateNewAccount("test@example.com"))
                .thenReturn(new UserCreatedWhenRegisteredDto(testUser, false, null));
        when(notificationFacade.sendAnEmailWhenClientHasAccount(any(), any(), any(), any()))
                .thenReturn(new NotificationFacadeResponse(true));

        // when
        ReservationFacadeResponse response = reservationFacade.createNewReservation(validDto);

        // then
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldReturnNearestAvailableTerms() {
        // given
        Reservation testReservation = createTestReservation();


        reservationRepository.save(testReservation);

        when(employeeFacade.getAvailableHours(any()))
                .thenReturn(List.of(
                        new AvailableTermDto(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                        new AvailableTermDto(LocalTime.of(10, 0), LocalTime.of(11, 0))
                ));

        // when
        List<AvailableTermWithDateDto> result = reservationFacade.getNearest5AvailableHours(1L);

        // then
        assertThat(result).hasSize(5);
        assertThat(result.get(0).date()).isEqualTo(testReservation.getReservationDateTime().toLocalDate());
    }


    @Test
    void shouldReturnUserReservations() {
        // given
        String testEmail = "user@example.com";
        User testUser = new User();
        testUser.setEmail(testEmail);
        testUser.setName("Test");
        testUser.setId(1L);

        User employeeUser = new User();
        employeeUser.setName("Employee");

        Employee testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setUser(employeeUser);

        Reservation testReservation1 = createTestReservation(testUser, LocalDateTime.now().plusDays(1), testEmployee);
        Reservation testReservation2 = createTestReservation(testUser, LocalDateTime.now().plusDays(1), testEmployee);

        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);

        when(userFacade.getUserByEmail(testEmail)).thenReturn(testUser);

        // when
        List<UserReservationDataDto> result = reservationFacade.getUserReservation(testEmail);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListForUserWithoutReservations() {
        // given
        String testEmail = "noreservations@example.com";
        User testUser = new User();
        testUser.setEmail(testEmail);

        when(userFacade.getUserByEmail(testEmail)).thenReturn(testUser);

        // when
        List<UserReservationDataDto> result = reservationFacade.getUserReservation(testEmail);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTomorrowReservations() {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        reservationRepository.save(createTestReservation(
                new User(),
                tomorrow.atTime(10, 0)
        ));
        reservationRepository.save(createTestReservation(
                new User(),
                tomorrow.atTime(14, 0)
        ));
        reservationRepository.save(createTestReservation(
                new User(),
                LocalDate.now().plusDays(2).atTime(10, 0)
        ));

        // when
        List<ReservationToTomorrow> result = reservationFacade.getAllReservationToTomorrow();

        // then
        assertThat(result)
                .hasSize(2);

    }

    @Test
    void shouldReturnEmptyListWhenNoTomorrowReservations() {
        // given
        reservationRepository.save(createTestReservation(
                new User(),
                LocalDate.now().minusDays(1).atTime(10, 0)
        ));

        // when
        List<ReservationToTomorrow> result = reservationFacade.getAllReservationToTomorrow();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteExistingReservation() {
        // given
        User testUser = new User();
        testUser.setEmail("test@example.com");
        Reservation testReservation = createTestReservation(testUser, LocalDateTime.now().plusDays(1));
        reservationRepository.save(testReservation);

        DeleteReservationDto dto = new DeleteReservationDto(1L, "test@example.com");
        when(userFacade.getUserByEmail("test@example.com")).thenReturn(testUser);

        // when
        ReservationFacadeResponse response = reservationFacade.deleteReservation(dto);

        // then
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldFailToDeleteNonExistingReservation() {
        // given
        DeleteReservationDto dto = new DeleteReservationDto(999L, "test@example.com");
        when(userFacade.getUserByEmail("test@example.com")).thenReturn(new User());

        // when
        Exception exception = assertThrows(ReservationDeleteException.class, () -> reservationFacade.deleteReservation(dto));

        // then
        assertThat(exception.getMessage()).isEqualTo("Reservation not found");
    }

    @Test
    void shouldUpdateReservationDate() {
        // given
        User testUser = new User();
        testUser.setName("Test");
        testUser.setEmail("test@example.pl");

        LocalDateTime newDate = LocalDateTime.now().plusDays(2);
        Reservation testReservation = createTestReservation(testUser, LocalDateTime.now().plusDays(1));
        reservationRepository.save(testReservation);

        UpdateReservationDto dto = new UpdateReservationDto(1L, newDate);
        when(userFacade.getUserByEmail(anyString())).thenReturn(testReservation.getUser());

        // when
        UserReservationDto result = reservationFacade.updateReservationDate(dto);

        // then
        assertThat(result.reservationDateTime()).isEqualTo(newDate);
    }

    @Test
    void shouldReturnReservationsGroupedByDateForSalon() {
        // given
        Long salonId = 1L;
        LocalDate today = LocalDate.now();

        reservationRepository.save(createSalonReservation(salonId, today.atTime(10, 0)));
        reservationRepository.save(createSalonReservation(salonId, today.atTime(14, 0)));
        reservationRepository.save(createSalonReservation(2L, today.atTime(16, 0)));

        // when
        Map<LocalDate, List<ReservationDto>> result = reservationFacade.getAllReservationBySalonId(salonId);

        // then
        assertThat(result)
                .hasSize(1)
                .containsKey(today)
                .satisfies(entries -> {
                    assertThat(entries.get(today)).hasSize(2);
                });
    }

    @Test
    void shouldReturnEmployeeBusyTerms() {
        // given
        Long employeeId = 1L;
        LocalDate date = LocalDate.now();

        reservationRepository.save(createEmployeeReservation(employeeId, date.atTime(10, 0)));
        reservationRepository.save(createEmployeeReservation(employeeId, date.atTime(14, 0)));

        // when
        List<AvailableTermDto> result = reservationFacade.getEmployeeBusyTerm(employeeId, date);

        // then
        assertThat(result)
                .hasSize(2)
                .extracting(AvailableTermDto::startServices)
                .containsExactly(LocalTime.of(10, 0), LocalTime.of(14, 0));
    }

    private Reservation createSalonReservation(Long salonId, LocalDateTime dateTime) {
        Salon salon = new Salon();
        salon.setId(salonId);


        User testUser = new User();
        testUser.setEmail("test@example.pl");
        testUser.setName("Test");

        User employeeUser = new User();
        employeeUser.setName("Employee");
        Employee employee = new Employee();
        employee.setUser(employeeUser);



        Reservation reservation = createTestReservation(testUser, dateTime,employee);
        reservation.setSalon(salon);
        return reservation;
    }

    private Reservation createEmployeeReservation(Long employeeId, LocalDateTime dateTime) {
        Employee employee = new Employee();
        employee.setId(employeeId);

        Reservation reservation = createTestReservation(new User(), dateTime);
        reservation.setEmployee(employee);
        return reservation;
    }


    private Reservation createTestReservation() {
        Reservation reservation = new Reservation();
        reservation.setSalon(new Salon());
        reservation.setEmployee(new Employee());
        reservation.setOffer(new Offer());
        reservation.setUser(new User());
        reservation.setReservationDateTime(validDateTime);
        reservation.setId(1L);
        return reservation;
    }

    private Reservation createTestReservation(User user, LocalDateTime dateTime) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setReservationDateTime(dateTime);
        reservation.setSalon(new Salon());
        reservation.setEmployee(new Employee());
        Offer offer = new Offer();
        offer.setDuration(LocalTime.of(1, 0));
        reservation.setOffer(offer);
        return reservation;
    }

    private Reservation createTestReservation(User user, LocalDateTime dateTime, Employee employee) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setReservationDateTime(dateTime);
        reservation.setSalon(new Salon());
        reservation.setEmployee(employee);
        Offer offer = new Offer();
        offer.setDuration(LocalTime.of(1, 0));
        reservation.setOffer(offer);
        return reservation;
    }

}