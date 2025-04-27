package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.employeemodule.Employee;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.offermodule.Offer;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    ReservationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_return_empty_list_when_no_reservation() {
        //given
        Long employeeId = 1L;
        LocalDate date = LocalDate.of(2024,11,13);

        //when
        List<AvailableTermDto> employeeBusyTerms = reservationService.getEmployeeBusyTerms(employeeId, date);

        //then
        assertThat(employeeBusyTerms).isEmpty();
    }

    @Test
    void should_return_a_list_with_1_element() {
        //given
        Long employeeId = 1L;
        LocalDate date = LocalDate.of(2024, 11, 13);

        List<Reservation> reservations = prepareListWithOneReservation();
        when(reservationRepository.findAll()).thenReturn(reservations);

        //when

        List<AvailableTermDto> employeeBusyTerms = reservationService.getEmployeeBusyTerms(employeeId, date);

        //then
        assertThat(employeeBusyTerms).isNotEmpty();
        assertThat(employeeBusyTerms.size()).isEqualTo(1);

        AvailableTermDto term = employeeBusyTerms.get(0);
        assertThat(term.startServices()).isEqualTo(LocalTime.of(12, 0));
        assertThat(term.endServices()).isEqualTo(LocalTime.of(12, 30));
    }

    @Test
    void should_return_a_list_with_2_element() {
        //given
        Long employeeId = 1L;
        LocalDate date = LocalDate.of(2024, 11, 13);
        List<Reservation> reservations = prepareListWithTwoReservation();

        when(reservationRepository.findAll()).thenReturn(reservations);

        //when
        List<AvailableTermDto> employeeBusyTerms = reservationService.getEmployeeBusyTerms(employeeId, date);

        //then
        assertThat(employeeBusyTerms).isNotEmpty();
        assertThat(employeeBusyTerms.size()).isEqualTo(2);
    }

    private List<Reservation> prepareListWithTwoReservation() {
        Employee mockEmployee = createMockEmployee(1L);

        Reservation mockReservation1 = createMockReservation(
                mockEmployee,
                LocalDateTime.of(2024, 11, 13, 12, 0),
                30
        );

        Reservation mockReservation2 = createMockReservation(
                mockEmployee,
                LocalDateTime.of(2024, 11, 13, 12, 30),
                30
        );

        return List.of(mockReservation1, mockReservation2);

    }

    private List<Reservation> prepareListWithOneReservation() {
        Employee mockEmployee = createMockEmployee(1L);
        Reservation mockReservation = createMockReservation(
                mockEmployee,
                LocalDateTime.of(2024, 11, 13, 12, 0),
                30
        );

        return List.of(mockReservation);

    }

    private Employee createMockEmployee(Long id) {
        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(id);
        return employee;
    }

    private Reservation createMockReservation(Employee employee, LocalDateTime dateTime, int durationMinutes) {
        Offer offer = mock(Offer.class);
        when(offer.getDuration()).thenReturn(LocalTime.of(0, durationMinutes));

        Reservation reservation = mock(Reservation.class);
        when(reservation.getEmployee()).thenReturn(employee);
        when(reservation.getReservationDateTime()).thenReturn(dateTime);
        when(reservation.getOffer()).thenReturn(offer);

        return reservation;
    }

}