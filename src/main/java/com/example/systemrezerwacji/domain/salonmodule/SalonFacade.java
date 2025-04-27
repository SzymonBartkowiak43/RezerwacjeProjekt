package com.example.systemrezerwacji.domain.salonmodule;


import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeWithAllInformationDto;
import com.example.systemrezerwacji.domain.employeemodule.response.CreateEmployeeResponseDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeDto;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.openinghoursmodule.dto.OpeningHoursDto;
import com.example.systemrezerwacji.domain.reservationmodule.ReservationFacade;
import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationDto;
import com.example.systemrezerwacji.domain.salonmodule.dto.*;
import com.example.systemrezerwacji.domain.salonmodule.exception.SalonCreationException;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import com.example.systemrezerwacji.domain.openinghoursmodule.OpeningHoursFacade;
import com.example.systemrezerwacji.domain.salonmodule.dto.AddHoursResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class SalonFacade {

    private final UserFacade userFacade;
    private final OpeningHoursFacade openingHoursFacade;
    private final EmployeeFacade employeeFacade;
    private final OfferFacade offerFacade;
    private final ReservationFacade reservationFacade;

    private final SalonCreator salonCreator;
    private final SalonService salonService;


    public SalonFacadeResponseDto createNewSalon(CreateNewSalonDto salonDto) {
        try {
            Long salonId = salonCreator.create(salonDto);
            return new SalonFacadeResponseDto(SalonValidationResult.SUCCESS_MESSAGE, salonId);
        } catch (SalonCreationException e) {
            return new SalonFacadeResponseDto(e.getMessage(), null);
        }
    }

    public SalonFacadeResponseDto addOpeningHoursToSalon(List<OpeningHoursDto> openingHours) {
        Long salonId = openingHours.get(0).salonId();
        Salon salon = salonService.getSalon(salonId);

        AddHoursResponseDto response = openingHoursFacade.addOpeningHours(openingHours, salon);
        salon.addOpeningHours(response.openingHours());

        return new SalonFacadeResponseDto("success", salonId);
    }

    public CreateEmployeeResponseDto addEmployeeToSalon(EmployeeDto employeeDto) {
        Salon salon= salonService.getSalon(employeeDto.salonId());

        return employeeFacade.createEmployeeAndAddToSalon(employeeDto, salon);
    }

    public SalonOffersListDto getAllOffersToSalon(Long salonId) {
        List<OfferDto> allOffers = offerFacade.getAllOffersToSalon(salonId);

        return new SalonOffersListDto("success", allOffers);
    }

    @Cacheable(value = "salons")
    public List<SalonWithIdDto> getAllSalons() {
        return salonService.getAllSalons();
    }

    public Optional<SalonWithIdDto> getSalonById(Long id) {
        return salonService.getSalonById(id);
    }


    public Salon getSalon(Long id) {
        return salonService.getSalon(id);
    }

//*******************************IMAGE*******************************************
    public void addImageToSalon(Long salonId, Image image) {
        Salon salon = salonService.addImageToSalon(salonId, image);
    }

    public List<ImageDto> findImagesBySalonId(Long salonId) {
        return salonService.findImagesBySalonId(salonId);

    }
//*******************************OWNER*******************************************
    public List<SalonWithIdDto> getAllSalonsToOwner(String email) {
        User user = userFacade.getUserByEmail(email);
        List<SalonWithIdDto> allSalons =  salonService.getAllSalons(user);
        return allSalons;
    }

    public OwnerSalonWithAllInformation getSalonByIdToOwner(Long salonId, String email) {
        Map<LocalDate, List<ReservationDto>> reservationDto = reservationFacade.getAllReservationBySalonId(salonId);
        List<EmployeeWithAllInformationDto> employeeDto = employeeFacade.getAllEmployees(salonId);
        List<OfferDto> offerDto = offerFacade.getAllOffersToSalon(salonId);
        String salonName = salonService.getSalon(salonId).getSalonName();

        return new OwnerSalonWithAllInformation(reservationDto, employeeDto, offerDto, salonName);
    }
}






























