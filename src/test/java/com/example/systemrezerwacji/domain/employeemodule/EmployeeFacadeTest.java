package com.example.systemrezerwacji.domain.employeemodule;

import com.example.systemrezerwacji.domain.employeemodule.dto.*;
import com.example.systemrezerwacji.domain.employeemodule.response.CreateEmployeeResponseDto;
import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.reservationmodule.ReservationFacade;
import com.example.systemrezerwacji.domain.reservationmodule.dto.AvailableDatesReservationDto;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeFacadeTest {

    EmployeeRepository employeeRepository = new EmployeeRepositoryTestImpl();
    EmployeeAvailabilityRepository employeeAvailabilityRepository = new EmployeeAvailabilityRepositoryTestImpl();

    @Mock
    UserFacade userFacade;

    @Mock
    OfferFacade offerFacade;

    @Mock
    ReservationFacade reservationFacade;

    EmployeeFacade employeeFacade;

    @BeforeEach
    void setUp() {
        EmployeeConfiguration configuration = new EmployeeConfiguration();
        configuration.userFacade = userFacade;
        configuration.offerFacade = offerFacade;
        configuration.reservationFacade = reservationFacade;
        employeeFacade = configuration.createForTest(employeeRepository, employeeAvailabilityRepository);
    }

    @Test
    void should_return_success_response_when_employee_creation_succeeded() {
        // given
        Salon salon = new Salon();
        EmployeeDto employeeDto = EmployeeDto.builder()
                .email("test@example.com")
                .name("John")
                .salonId(1L)
                .availability(List.of())
                .build();

        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        when(userFacade.createEmployee(any(EmployeeDto.class))).thenReturn(Optional.of(testUser));

        // when
        CreateEmployeeResponseDto result = employeeFacade.createEmployeeAndAddToSalon(employeeDto, salon);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.employeeEmail()).isEqualTo("test@example.com");
        assertThat(employeeRepository.findAll()).hasSize(1);
    }

    @Test
    void should_return_employees_with_offers_for_given_offer_id() {
        // given
        User user = new User();
        user.setName("John Doe");
        user.setId(1L);
        when(userFacade.getEmployeeNameById(any())).thenReturn(Map.of(1L, "John"));

        Offer offer = new Offer();
        offer.setId(1L);


        Employee employee = new Employee();
        employee.setId(1L);
        employee.setUser(user);
        employee.setOffers(List.of(offer));
        employeeRepository.save(employee);

        // when
        List<EmployeeToOfferDto> result = employeeFacade.getEmployeesToOffer(1L);

        // then
        assertThat(result)
                .hasSize(1)
                .extracting(EmployeeToOfferDto::name)
                .containsExactly("John");
    }

    @Test
    void should_return_available_hours_excluding_past_times_for_today() {
        // given
        AvailableDatesReservationDto dto = new AvailableDatesReservationDto(
                LocalDate.of(2026,2,2), 1L, 1L
        );

        EmployeeAvailability availability = new EmployeeAvailability();
        availability.setId(1L);
        availability.setEmployee(new Employee());
        availability.setDayOfWeek(DayOfWeek.valueOf("MONDAY"));
        availability.setStartTime(LocalTime.of(9,0));
        availability.setEndTime(LocalTime.of(17,0));


        User user = new User();
        user.setName("John");
        user.setId(1L);


        Offer offer = new Offer();
        offer.setId(1L);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setUser(user);
        employee.setOffers(List.of(offer));
        employee.setAvailability(List.of(availability));
        employeeRepository.save(employee);

        availability.setEmployee(employee);
        employeeAvailabilityRepository.save(availability);

        when(offerFacade.getDurationToOffer(1L)).thenReturn(LocalTime.of(1, 0));
        when(reservationFacade.getEmployeeBusyTerm(any(), any())).thenReturn(List.of());

        // when
        List<AvailableTermDto> result = employeeFacade.getAvailableHours(dto);

        // then
        assertThat(result)
                .extracting(AvailableTermDto::startServices)
                .size().isGreaterThan(10);
    }

    @Test
    void should_add_offer_to_employee_successfully() {
        // given
        Offer testOffer = new Offer();
        testOffer.setId(1L);
        when(offerFacade.getOffer(1L)).thenReturn(testOffer);

        Employee employee = new Employee();
        employee.setId(1L);
        employeeRepository.save(employee);

        // when
        EmployeeFacadeResponseDto result = employeeFacade.addOfferToEmployee(1L, 1L);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(employeeRepository.findById(1L).get().getOffers())
                .contains(testOffer);
    }

    @Test
    void should_retrieve_employee_by_id() {
        // given
        Employee expected = new Employee();
        expected.setId(1L);
        employeeRepository.save(expected);

        // when
        Employee result = employeeFacade.getEmployee(1L);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void should_return_all_employees_for_salon() {
        // given
        Salon salon1 = new Salon();
        salon1.setId(1L);

        User user = new User();
        user.setName("John");
        user.setId(1L);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setUser(user);
        employeeRepository.save(employee);
        employee.setSalon(salon1);

        employeeRepository.save(employee);

        // when
        List<EmployeeWithAllInformationDto> result = employeeFacade.getAllEmployees(1L);

        // then
        assertThat(result).hasSize(2);
    }
}