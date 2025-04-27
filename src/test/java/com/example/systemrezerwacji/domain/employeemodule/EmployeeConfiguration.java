package com.example.systemrezerwacji.domain.employeemodule;

import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.reservationmodule.ReservationFacade;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EmployeeConfiguration {

    @Mock
    UserFacade userFacade;

    @Mock
    OfferFacade offerFacade;

    @Mock
    ReservationFacade reservationFacade;

    public EmployeeConfiguration() {
        MockitoAnnotations.openMocks(this);
    }

    EmployeeFacade createForTest(EmployeeRepository employeeRepository,EmployeeAvailabilityRepository employeeAvailabilityRepository) {
        EmployeeAvailabilityService employeeAvailabilityService = createEmployeeAvailabilityServiceForTest(employeeAvailabilityRepository);
        EmployeeService employeeService = new EmployeeService(employeeRepository,employeeAvailabilityService);
        return new EmployeeFacade(userFacade,offerFacade,reservationFacade,employeeService);
    }

    private EmployeeAvailabilityService createEmployeeAvailabilityServiceForTest(EmployeeAvailabilityRepository employeeAvailabilityRepository) {
        return new EmployeeAvailabilityService(employeeAvailabilityRepository);
    }
}
