package com.example.systemrezerwacji.domain.salonmodule;

import com.example.systemrezerwacji.domain.codemodule.CodeFacade;
import com.example.systemrezerwacji.domain.codemodule.message.ConsumeMessage;
import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeWithAllInformationDto;
import com.example.systemrezerwacji.domain.employeemodule.response.CreateEmployeeResponseDto;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.offermodule.dto.OfferDto;
import com.example.systemrezerwacji.domain.openinghoursmodule.OpeningHoursFacade;
import com.example.systemrezerwacji.domain.openinghoursmodule.dto.OpeningHoursDto;
import com.example.systemrezerwacji.domain.reservationmodule.ReservationFacade;
import com.example.systemrezerwacji.domain.reservationmodule.dto.ReservationDto;
import com.example.systemrezerwacji.domain.salonmodule.dto.*;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class SalonFacadeTest {

    SalonRepository salonRepository = new SalonRepositoryTestImpl();
    ImageRepository imageRepository = new ImageRepositoryTestImpl();


    @Mock
    private UserFacade userFacade;

    @Mock
    private CodeFacade codeFacade;

    @Mock
    OpeningHoursFacade openingHoursFacade;

    @Mock
    EmployeeFacade employeeFacade;

    @Mock
    OfferFacade offerFacade;

    @Mock
    ReservationFacade reservationFacade;

    private SalonFacade salonFacade;


    @BeforeEach
    void setUp() {
        SalonConfiguration salonConfiguration = new SalonConfiguration();
        userFacade = salonConfiguration.userFacade;
        codeFacade = salonConfiguration.codeFacade;
        openingHoursFacade = salonConfiguration.openingHoursFacade;
        employeeFacade = salonConfiguration.employeeFacade;
        offerFacade = salonConfiguration.offerFacade;
        reservationFacade = salonConfiguration.reservationFacade;

        salonFacade = salonConfiguration.createForTest(salonRepository, imageRepository);

    }

    private final Long testSalonId = 1L;
    private final String testEmail = "owner@example.com";

    @Test
    void shouldCreateNewSalonSuccessfully() {
        // given
        CreateNewSalonDto dto = new CreateNewSalonDto("Test Salon", "Test Category", "Bialystok", "12-345", "Test Street", "1", testEmail, "1234");
        User user = new User();
        user.setId(1L);
        user.setName("Owner");
        user.setEmail(testEmail);


        when(codeFacade.consumeCode(any(), any())).thenReturn(new ConsumeMessage("success", true));
        when(userFacade.addUserRoleOwner(any())).thenReturn(Optional.of(user));
        when(userFacade.getUserByEmail(any())).thenReturn(user);

        // when
        SalonFacadeResponseDto result = salonFacade.createNewSalon(dto);

        // then
        assertThat(result.message()).isEqualTo(SalonValidationResult.SUCCESS_MESSAGE);
        ;
    }

    @Test
    void shouldHandleSalonCreationFailure() {
        // given
        CreateNewSalonDto dto = new CreateNewSalonDto("", "", "Bialystok", "", "Test Street", "1", testEmail, "1234");

        // when
        SalonFacadeResponseDto result = salonFacade.createNewSalon(dto);

        // then
        assertThat(result.message()).contains("Salon name should be longer");
        assertThat(result.salonId()).isNull();
    }

    @Test
    void shouldAddOpeningHoursToSalon() {
        // given
        List<OpeningHoursDto> hours = List.of(new OpeningHoursDto(testSalonId, "MONDAY", LocalTime.of(8, 0), LocalTime.of(20, 0)));
        Salon salon = new Salon();
        salon.setId(1L);
        salonRepository.save(salon);

        when(openingHoursFacade.addOpeningHours(anyList(), any())).thenReturn(new AddHoursResponseDto("success", List.of()));

        // when
        SalonFacadeResponseDto result = salonFacade.addOpeningHoursToSalon(hours);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.salonId()).isEqualTo(testSalonId);
    }

    @Test
    void shouldAddEmployeeToSalon() {
        // given
        EmployeeDto dto = new EmployeeDto(testSalonId, "John", "Doe", List.of());
        CreateEmployeeResponseDto expected = new CreateEmployeeResponseDto("success", "john@example.com", "password");

        Salon salon = new Salon();
        salon.setId(1L);
        salonRepository.save(salon);

        when(employeeFacade.createEmployeeAndAddToSalon(any(), any())).thenReturn(expected);

        // when
        CreateEmployeeResponseDto result = salonFacade.addEmployeeToSalon(dto);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldGetAllOffersForSalon() {
        // given
        List<OfferDto> offers = List.of(new OfferDto(1L, "Offer 1", "Description", BigDecimal.valueOf(200.0), LocalTime.of(1, 0)));
        when(offerFacade.getAllOffersToSalon(testSalonId)).thenReturn(offers);

        // when
        SalonOffersListDto result = salonFacade.getAllOffersToSalon(testSalonId);

        // then
        assertThat(result.offers()).hasSize(1);
        assertThat(result.message()).isEqualTo("success");
    }

    @Test
    void shouldGetAllSalons() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Owner");
        user.setEmail(testEmail);
        Salon salon = new Salon();
        salon.setId(1L);
        salon.setUser(user);
        salonRepository.save(salon);

        // when
        List<SalonWithIdDto> result = salonFacade.getAllSalons();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("1");
    }

    @Test
    void shouldGetSalonById() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Owner");
        user.setEmail(testEmail);
        Salon salon = new Salon();
        salon.setId(1L);
        salon.setUser(user);
        salonRepository.save(salon);

        // when
        Optional<SalonWithIdDto> result = salonFacade.getSalonById(testSalonId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(testSalonId.toString());
    }

    @Test
    void shouldAddImageToSalon() {
        // given
        Salon salon = new Salon();
        salon.setId(testSalonId);
        salonRepository.save(salon);

        Image image = new Image();
        image.setName("test.jpg");
        image.setSalon(salon);
        imageRepository.save(image);

        // when
        salonFacade.addImageToSalon(testSalonId, image);

        // then
        List<Image> images = imageRepository.findBySalonId(testSalonId);
        assertThat(images).hasSize(1);
        assertThat(images.get(0).getName()).isEqualTo("test.jpg");
    }

    @Test
    void shouldGetImagesForSalon() {
        // given
        Salon salon = new Salon();
        salon.setId(testSalonId);
        salonRepository.save(salon);

        Image image = new Image();
        image.setName("test.jpg");
        image.setSalon(salon);
        imageRepository.save(image);

        // when
        List<ImageDto> result = salonFacade.findImagesBySalonId(testSalonId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("test.jpg");
    }

    @Test
    void shouldGetSalonsForOwner() {
        // given
        User owner = new User();
        owner.setEmail(testEmail);
        owner.setId(1L);

        Salon salon = new Salon();
        salon.setId(testSalonId);
        salon.setUser(owner);
        salonRepository.save(salon);

        when(userFacade.getUserByEmail(testEmail)).thenReturn(owner);

        // when
        List<SalonWithIdDto> result = salonFacade.getAllSalonsToOwner(testEmail);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(testSalonId.toString());
    }

    @Test
    void shouldGetFullSalonDetailsForOwner() {
        // given
        User owner = new User();
        owner.setEmail(testEmail);

        Salon salon = new Salon();
        salon.setId(testSalonId);
        salon.setSalonName("Test Salon");
        salonRepository.save(salon);

        when(reservationFacade.getAllReservationBySalonId(testSalonId))
                .thenReturn(Map.of(LocalDate.now(), List.of(new ReservationDto(1L,"Employee name", "Offer name", BigDecimal.valueOf(200), LocalDateTime.of(2026,2,2,10,20), LocalDateTime.of(2026,2,2,10,20)))));

        when(employeeFacade.getAllEmployees(testSalonId))
                .thenReturn(List.of(new EmployeeWithAllInformationDto(1L,1L,"Test", "email", List.of(), List.of())));

        when(offerFacade.getAllOffersToSalon(testSalonId))
                .thenReturn(List.of(new OfferDto(1L, "Offer 1", "Description", BigDecimal.valueOf(200.0), LocalTime.of(1, 0))));

        // when
        OwnerSalonWithAllInformation result = salonFacade.getSalonByIdToOwner(testSalonId, testEmail);

        // then
        assertThat(result.salonName()).isEqualTo("Test Salon");
        assertThat(result.employeeDto()).isNotEmpty();
        assertThat(result.offerDto()).isNotEmpty();

    }
}
