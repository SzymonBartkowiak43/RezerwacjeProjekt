package com.example.systemrezerwacji.domain.employeemodule;

import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeAvailabilityDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeWithAllInformationDto;
import com.example.systemrezerwacji.domain.employeemodule.exception.EmployeeDuplicateOfferException;
import com.example.systemrezerwacji.domain.offermodule.Offer;
import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.usermodule.User;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class EmployeeService {
    private static final Logger log = LogManager.getLogger(EmployeeService.class);
    public static final int MINUTES_IN_INTERVAL = 15;

    private final EmployeeRepository employeeRepository;
    private final EmployeeAvailabilityService employeeAvailabilityService;

    EmployeeService(EmployeeRepository employeeRepository, EmployeeAvailabilityService employeeAvailabilityService) {
        this.employeeRepository = employeeRepository;
        this.employeeAvailabilityService = employeeAvailabilityService;
        log.info("EmployeeService initialized");
    }

    Employee saveEmployee(Employee employee) {
        log.info("Employee saved with id: {}", employee.getId());
        return employeeRepository.save(employee);
    }


    List<EmployeeAvailability> createAvailabilityList(List<EmployeeAvailabilityDto> availabilityDto, Employee employee) {
        log.debug("Creating availability list for employeeId: {}", employee.getId());
        return availabilityDto.stream()
                .map(dto -> {
                    EmployeeAvailability availability = new EmployeeAvailability();
                    availability.setEmployee(employee);
                    availability.setDayOfWeek(DayOfWeek.valueOf(dto.dayOfWeek()));
                    availability.setStartTime(dto.startTime());
                    availability.setEndTime(dto.endTime());
                    return availability;
                })
                .toList();
    }

    List<Long> findEmployeesIdByOfferId(Long offerId) {
        log.info("Finding employees for offerId: {}", offerId);
        List<Employee> offersId = employeeRepository.findByOffersId(offerId);
        log.info("Found {} employees for offerId: {}", offersId.size(), offerId);
        return offersId.stream()
                .map(Employee::getId)
                .toList();
    }

    List<Long> findEmployeesUserIdById(List<Long> employeesId) {
        log.debug("Finding user IDs for employees: {}", employeesId);
        List<Employee> allById = (List<Employee>) employeeRepository.findAllById(employeesId);
        log.info("Resolved {} user IDs from {} employee IDs", allById.size(), employeesId.size());
        return allById.stream()
                .map(Employee::getUser)
                .map(User::getId)
                .toList();
    }


    List<AvailableTermDto> findAvailability(Long employeeId, LocalDate date, LocalTime duration, List<AvailableTermDto> employeeBusyTermsList) {
        log.info("Finding availability for employeeId: {} on {}", employeeId, date);

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (date.isBefore(LocalDate.now())) {
            log.warn("Requested availability for past date: {}", date);
            return new ArrayList<>();
        }
        EmployeeAvailability availability =  employeeAvailabilityService.findEmployeeAvability(employeeId, dayOfWeek);

        return generateAvailableTerms(availability.getStartTime(), availability.getEndTime(), duration, employeeBusyTermsList);
    }

    Long getUserIdByEmployeeId(Long employeeId) {
        Optional<Employee> byId = employeeRepository.findById(employeeId);
        return byId.get().getUser().getId();
    }

    Employee addOfferToEmployee(Long employeeId, Offer offer) {
        log.debug("Getting user ID for employeeId: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", employeeId);
                    return new IllegalArgumentException("Employee not found");
                });

        if(employee.getOffers().contains(offer)) {
            throw new EmployeeDuplicateOfferException();
        }

        employee.addOffer(offer);
        Employee save = employeeRepository.save(employee);
        return save;

    }

    Employee getEmployee(Long id) {
        return employeeRepository.findById(id).get();
    }


    private List<AvailableTermDto> generateAvailableTerms(LocalTime start, LocalTime end, LocalTime duration, List<AvailableTermDto> employeeBusyTermsList) {
        log.debug("Generating terms between {} and {} with duration {}", start, end, duration);
        List<AvailableTermDto> termsList = new ArrayList<>();

        int serviceDurationMinutes = duration.getHour() * 60 + duration.getMinute();
        Duration serviceDuration = Duration.ofMinutes(serviceDurationMinutes);

        LocalTime currentStart = start;

        while (!currentStart.plus(serviceDuration).isAfter(end)) {
            LocalTime currentEnd = currentStart.plus(serviceDuration);
            if (!currentEnd.isAfter(end)) {
                termsList.add(new AvailableTermDto(currentStart, currentEnd));
            }
            currentStart = currentStart.plusMinutes(MINUTES_IN_INTERVAL);
        }
        log.debug("Generated {} raw terms before filtering", termsList.size());
        return termsList.stream()
                .filter(term -> isTermAvailable(term, employeeBusyTermsList))
                .collect(Collectors.toList());
    }

    private boolean isTermAvailable(AvailableTermDto term, List<AvailableTermDto> busyTerms) {
        LocalTime termStart = term.startServices();
        LocalTime termEnd = term.endServices();

        return busyTerms.stream().noneMatch(busyTerm -> {
            LocalTime busyStart = busyTerm.startServices();
            LocalTime busyEnd = busyTerm.endServices();
            return termStart.isBefore(busyEnd) && termEnd.isAfter(busyStart);
        });
    }

    public List<EmployeeWithAllInformationDto> getAllEmployeesToSalon(Long salonId) {
        return employeeRepository.findAllBySalonId(salonId)
                .stream()
                .map(employee -> new EmployeeWithAllInformationDto(
                        employee.getId(),
                        employee.getSalon().getId(),
                        employee.getUser().getName(),
                        employee.getUser().getEmail(),
                        employee.getAvailability()
                                .stream()
                                .map(availability -> new EmployeeAvailabilityDto(
                                        availability.getDayOfWeek().name(),
                                        availability.getStartTime(),
                                        availability.getEndTime()))
                                .toList(),
                        employee.getOffers()
                                .stream()
                                .map(offer -> new OfferDto(
                                        offer.getId(),
                                        offer.getName(),
                                        offer.getDescription(),
                                        offer.getPrice(),
                                        offer.getDuration()))
                                .toList()
                ))
                .collect(Collectors.toList());
    }

}
