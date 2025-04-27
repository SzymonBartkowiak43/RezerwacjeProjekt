package com.example.systemrezerwacji.domain.employeemodule;

import com.example.systemrezerwacji.domain.employeemodule.dto.*;
import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.reservationmodule.ReservationFacade;
import com.example.systemrezerwacji.domain.employeemodule.response.CreateEmployeeResponseDto;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import com.example.systemrezerwacji.domain.reservationmodule.dto.AvailableDatesReservationDto;
import com.example.systemrezerwacji.domain.salonmodule.Salon;
import com.example.systemrezerwacji.domain.usermodule.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EmployeeFacade {

    private static final String USER_CREATION_FAILED = "User creation failed";
    private final UserFacade userFacade;
    private final OfferFacade offerFacade;
    private final ReservationFacade reservationFacade;
    private final EmployeeService employeeService;


    public CreateEmployeeResponseDto createEmployeeAndAddToSalon(EmployeeDto employeeDto, Salon salon) {
        Optional<User> userOptional = userFacade.createEmployee(employeeDto);
        if (userOptional.isEmpty()) {
            return CreateEmployeeResponseDto.builder()
                    .message(USER_CREATION_FAILED)
                    .build();
        }
        User user = userOptional.get();

        Employee employee = createEmployee(employeeDto, salon, user);
        Employee savedEmployee = employeeService.saveEmployee(employee);

        return CreateEmployeeResponseDto.builder()
                .message("success")
                .employeeEmail(user.getEmail())
                .employeePassword(user.getPassword())
                .build();
    }

    public List<EmployeeToOfferDto> getEmployeesToOffer(Long id) {
        List<Long> employeesId = employeeService.findEmployeesIdByOfferId(id);
        List<Long> employeesUserIdById = employeeService.findEmployeesUserIdById(employeesId);
        Map<Long, String> userIdAndName = userFacade.getEmployeeNameById(employeesUserIdById);

        return employeesId.stream()
                .map(employeeId -> {
                    Long userId = employeeService.getUserIdByEmployeeId(employeeId);
                    String name = userIdAndName.get(userId);
                    return new EmployeeToOfferDto(employeeId, name);
                })
                .toList();
    }

    public List<AvailableTermDto> getAvailableHours(AvailableDatesReservationDto availableDate) {
        LocalTime duration = offerFacade.getDurationToOffer(availableDate.offerId());
        List<AvailableTermDto> employeeBusyTermsList = reservationFacade.getEmployeeBusyTerm(availableDate.employeeId(), availableDate.date());
        List<AvailableTermDto> termsDto = employeeService.findAvailability(
                availableDate.employeeId(), availableDate.date(), duration, employeeBusyTermsList);

        if (availableDate.date().isEqual(LocalDate.now())) {
            LocalTime currentTime = LocalTime.now();
            termsDto = termsDto.stream()
                    .filter(term -> term.startServices().isAfter(currentTime))
                    .collect(Collectors.toList());
        }

        return termsDto;
    }

    @Transactional
    public EmployeeFacadeResponseDto addOfferToEmployee(Long employeeId, Long offerId) {
        Offer offer = offerFacade.getOffer(offerId);
        Employee employee = employeeService.addOfferToEmployee(employeeId, offer);

        return new EmployeeFacadeResponseDto("success", employee.getId());
    }

    public Employee getEmployee(Long id) {
        return employeeService.getEmployee(id);
    }

    private Employee createEmployee(EmployeeDto employeeDto, Salon salon, User user) {
        Employee employee = new Employee();
        employee.setSalonAndUser(salon, user);
        List<EmployeeAvailability> availabilityList = employeeService.createAvailabilityList(employeeDto.availability(), employee);
        employee.setAvailability(availabilityList);
        return employee;
    }

    public List<EmployeeWithAllInformationDto> getAllEmployees(Long salonId) {
        List<EmployeeWithAllInformationDto> allEmployees = employeeService.getAllEmployeesToSalon(salonId);
        return allEmployees;
    }
}
