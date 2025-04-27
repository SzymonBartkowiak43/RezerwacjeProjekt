package com.example.systemrezerwacji.infrastructure.restcontrollers;

import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.employeemodule.dto.AddOfferRequestDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeFacadeResponseDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeToOfferDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.AvailableDatesReservationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://164.90.190.165")
public class EmployeeController {
    private final EmployeeFacade employeeFacade;

    public EmployeeController(EmployeeFacade employeeFacade) {
        this.employeeFacade = employeeFacade;
    }

    @GetMapping("/employee-to-offer/{offerId}")
    public ResponseEntity<List<EmployeeToOfferDto>> getEmployeeToOffer(@PathVariable Integer offerId) {
        List<EmployeeToOfferDto> employeesToOffer = employeeFacade.getEmployeesToOffer(offerId.longValue());

        return ResponseEntity.ok(employeesToOffer);
    }

    @GetMapping("/employee/available-dates")
    public ResponseEntity<List<AvailableTermDto>> getAvailableHours(
            @RequestParam("date") String date,
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("offerId") Long offerId
    ) {
        AvailableDatesReservationDto availableDate = new AvailableDatesReservationDto(LocalDate.parse(date), employeeId, offerId);

        List<AvailableTermDto> availableHours = employeeFacade.getAvailableHours(availableDate);

        if (availableHours.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(availableHours);
    }

    @PatchMapping("/employees/add-offer")
    public ResponseEntity<EmployeeFacadeResponseDto> addOfferToEmployee(@RequestBody AddOfferRequestDto offerRequest) {
        EmployeeFacadeResponseDto response = employeeFacade.addOfferToEmployee(offerRequest.employeeId(), offerRequest.offerId());

        if (response.message().equals("success")) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
