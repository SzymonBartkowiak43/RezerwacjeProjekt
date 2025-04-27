package com.example.systemrezerwacji.domain.reservationmodule;

import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermWithDateDto;
import com.example.systemrezerwacji.domain.offermodule.OfferFacade;
import com.example.systemrezerwacji.domain.employeemodule.EmployeeFacade;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.*;
import com.example.systemrezerwacji.domain.reservationmodule.response.AvailableTermSearchCriteria;
import com.example.systemrezerwacji.domain.reservationmodule.response.NotificationResult;
import com.example.systemrezerwacji.domain.reservationmodule.response.ReservationEntities;
import com.example.systemrezerwacji.infrastructure.notificationmode.NotificationFacade;
import com.example.systemrezerwacji.infrastructure.notificationmode.response.NotificationFacadeResponse;
import com.example.systemrezerwacji.domain.reservationmodule.response.ReservationFacadeResponse;
import com.example.systemrezerwacji.domain.salonmodule.SalonFacade;
import com.example.systemrezerwacji.domain.usermodule.User;
import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
public class ReservationFacade {

    private static final String NOTIFICATION_ERROR = "Problem with sending notification";
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    private static final int MAX_TERMS = 5;

    private final UserFacade userFacade;
    private final OfferFacade offerFacade;
    private final SalonFacade salonFacade;
    private final EmployeeFacade employeeFacade;
    private final NotificationFacade notificationFacade;
    private final ReservationService reservationService;
    private final ReservationValidator validator;
    private final ReservationResponseFactory responseFactory;

    public ReservationFacade(
            @Lazy OfferFacade offerFacade,
            @Lazy UserFacade userFacade,
            @Lazy SalonFacade salonFacade,
            @Lazy EmployeeFacade employeeFacade,
            @Lazy NotificationFacade notificationFacade,
            ReservationService reservationService,
            ReservationValidator validator,
            ReservationResponseFactory responseFactory) {
        this.offerFacade = offerFacade;
        this.userFacade = userFacade;
        this.salonFacade = salonFacade;
        this.employeeFacade = employeeFacade;
        this.notificationFacade = notificationFacade;
        this.reservationService = reservationService;
        this.validator = validator;
        this.responseFactory = responseFactory;
    }

    @Transactional
    public ReservationFacadeResponse createNewReservation(CreateReservationDto dto) {
        ReservationValidationResult validationResult = validateReservation(dto);
        if (!validationResult.isValid()) {
            return responseFactory.createError(validationResult.message());
        }

        ReservationEntities entities = fetchReservationEntities(dto);
        NotificationResult notificationResult = sendReservationNotification(dto, entities);

        return handleReservationCreation(dto, entities, notificationResult);
    }

    public List<AvailableTermWithDateDto> getNearest5AvailableHours(Long reservationId) {
        Reservation reservation = reservationService.getReservation(reservationId);
        AvailableTermSearchCriteria criteria = new AvailableTermSearchCriteria(
                reservation.getEmployee().getId(),
                reservation.getOffer().getId(),
                reservation.getReservationDateTime().toLocalDate()
        );

        return new AvailableTermFinder(employeeFacade)
                .findNearestAvailableTerms(criteria, MAX_TERMS);
    }

    public List<UserReservationDataDto> getUserReservation(String email) {
        User user = userFacade.getUserByEmail(email);
        return reservationService.getReservationToCurrentUser(user);
    }

    public List<ReservationToTomorrow> getAllReservationToTomorrow() {
        return reservationService.getAllReservationToTomorrow();
    }

    public ReservationFacadeResponse deleteReservation(DeleteReservationDto dto) {
        User user = userFacade.getUserByEmail(dto.userEmail());
        boolean isDeleted = reservationService.deleteReservation(dto.reservationId(), user);
        return responseFactory.createSimpleResponse(isDeleted);
    }

    public UserReservationDto updateReservationDate(UpdateReservationDto dto) {
        Reservation reservation = reservationService.getReservation(dto.reservationId());
        User user = userFacade.getUserByEmail(reservation.getUser().getEmail());
        return reservationService.updateReservationDate(dto.reservationId(), user, dto.newReservationDate());
    }

    public Map<LocalDate, List<ReservationDto>> getAllReservationBySalonId(Long salonId) {
        return reservationService.getAllReservationBySalonId(salonId);
    }

    public List<AvailableTermDto> getEmployeeBusyTerm(Long employeeId, LocalDate date) {
        return reservationService.getEmployeeBusyTerms(employeeId, date);
    }
/*****************************Private Method *********************************/

    private ReservationValidationResult validateReservation(CreateReservationDto dto) {
        LocalTime duration = offerFacade.getDurationToOffer(dto.offerId());
        return validator.validate(dto, duration);
    }

    private ReservationEntities fetchReservationEntities(CreateReservationDto dto) {
        return new ReservationEntities(
                salonFacade.getSalon(dto.salonId()),
                employeeFacade.getEmployee(dto.employeeId()),
                offerFacade.getOffer(dto.offerId()),
                userFacade.getUserByEmailOrCreateNewAccount(dto.userEmail())
        );
    }

    private NotificationResult sendReservationNotification(
            CreateReservationDto dto,
            ReservationEntities entities
    ) {
        if (entities.userInfo().isNewUser()) {
            return sendNewUserNotification(dto, entities);
        }
        return sendExistingUserNotification(dto, entities);
    }

    private NotificationResult sendNewUserNotification(
            CreateReservationDto dto,
            ReservationEntities entities
    ) {
        NotificationFacadeResponse response = notificationFacade.sendAnEmailWhenClientDoNotHasAccount(
                dto.userEmail(),
                entities.offer().getName(),
                entities.userInfo().unHashedPassword(),
                dto.reservationDateTime(),
                entities.salon().getSalonName()
        );
        return new NotificationResult(response.isSuccess(), entities.userInfo());
    }

    private NotificationResult sendExistingUserNotification(
            CreateReservationDto dto,
            ReservationEntities entities
    ) {
        NotificationFacadeResponse response = notificationFacade.sendAnEmailWhenClientHasAccount(
                dto.userEmail(),
                entities.offer().getName(),
                dto.reservationDateTime(),
                entities.salon().getSalonName()
        );
        return new NotificationResult(response.isSuccess(), entities.userInfo());
    }

    private ReservationFacadeResponse handleReservationCreation(
            CreateReservationDto dto,
            ReservationEntities entities,
            NotificationResult notificationResult
    ) {
        if (!notificationResult.success()) {
            return responseFactory.createError(NOTIFICATION_ERROR);
        }

        reservationService.addNewReservation(
                entities.salon(),
                entities.employee(),
                entities.userInfo().user(),
                entities.offer(),
                dto.reservationDateTime()
        );

        return responseFactory.createSuccess(
                SUCCESS,
                notificationResult.userInfo().unHashedPassword()
        );
    }

}
