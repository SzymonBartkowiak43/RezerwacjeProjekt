package com.example.systemrezerwacji.features;

import com.example.systemrezerwacji.BaseIntegrationTest;

import com.example.systemrezerwacji.domain.codemodule.dto.CodeDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.AvailableTermDto;
import com.example.systemrezerwacji.domain.employeemodule.dto.EmployeeFacadeResponseDto;
import com.example.systemrezerwacji.domain.employeemodule.response.CreateEmployeeResponseDto;
import com.example.systemrezerwacji.domain.offermodule.response.OfferFacadeResponse;
import com.example.systemrezerwacji.domain.reservationmodule.dto.UserReservationDataDto;
import com.example.systemrezerwacji.domain.reservationmodule.dto.UserReservationDto;
import com.example.systemrezerwacji.domain.reservationmodule.response.ReservationFacadeResponse;
import com.example.systemrezerwacji.domain.salonmodule.dto.SalonFacadeResponseDto;
import com.example.systemrezerwacji.domain.salonmodule.dto.SalonWithIdDto;

import com.example.systemrezerwacji.domain.usermodule.response.UserFacadeResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.regex.Pattern;

import com.example.systemrezerwacji.infrastructure.loginandregister.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TypicalScenarioWhenUserCreatedSallonIntegrationTest extends BaseIntegrationTest {

    @Test
    void user_patch_to_create_a_salon() throws Exception {
//        step 1: user tried to get JWT token by requesting POST /token with email=someUser, password=somePassword and system returned UNAUTHORIZED(401)
        // given && when
        ResultActions failedTokenRequest = mockMvc.perform(post("/token")
                .content("""
                        {
                        "email": "someEmail@some.pl",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        failedTokenRequest
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(
                        """
                                {
                                "message": "Bad Credentials",
                                "status": "UNAUTHORIZED"
                                }
                                """.trim()
                ));


//        step 2: user made GET /reservation/user/1 with no jwt token and system returned UNAUTHORIZED(401)
        // given && when
        ResultActions failedGetReservationToUser = mockMvc.perform(get("/reservation-service/code/get-link-to-code")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        failedGetReservationToUser.andExpect(status().isUnauthorized());


//        step 3: user made POST /register with email=someUser, password=somePassword and system registered user with status CREATED(201)
        // given && when
        ResultActions registerAction = mockMvc.perform(post("/register")
                .content("""
                        {
                        "name": "someName",
                        "email": "someEmail@some.pl",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        MvcResult registerResult = registerAction.andExpect(status().isCreated()).andReturn();
        String registerResultJson = registerResult.getResponse().getContentAsString();
        UserFacadeResponse userFacadeResponse = objectMapper.readValue(registerResultJson, UserFacadeResponse.class);
        assertAll(
                () -> assertThat(userFacadeResponse.userId()).isNotNull(),
                () -> assertThat(userFacadeResponse.message()).isEqualTo("success")
        );


//        step 4: user tried to get JWT token by requesting POST /token with email=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC
        // given && when
        ResultActions successLoginRequest = mockMvc.perform(post("/token")
                .content("""
                        {
                        "email": "someEmail@some.pl",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        MvcResult mvcResultSuccess = successLoginRequest.andExpect(status().isOk()).andReturn();
        String successJson = mvcResultSuccess.getResponse().getContentAsString();
        JwtResponseDto jwtResponse = objectMapper.readValue(successJson, JwtResponseDto.class);
        String token = jwtResponse.token();
        assertAll(
                () -> assertThat(jwtResponse.email()).isEqualTo("someEmail@some.pl"),
                () -> assertThat(token).matches(Pattern.compile("^([A-Za-z0-9-_=]+\\.)+([A-Za-z0-9-_=])+\\.?$"))
        );

//        step 5: generate Code to create Salon.
        // given && when
        ResultActions generateCode = mockMvc.perform(post("/reservation-service/code/generateCode")
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        // then
        MvcResult mvcCodeCreated = generateCode.andExpect(status().isCreated()).andReturn();
        String codeJson = mvcCodeCreated.getResponse().getContentAsString();
        CodeDto codeDto = objectMapper.readValue(codeJson, CodeDto.class);

        assertThat(codeDto.code()).isNotNull();
        assertThat(codeDto.isConsumed()).isFalse();


//        step 5.5: user made POST /salon with valid salon details and system created the salon, returning OK(200) with salonId=1
        // given
        String salonRequestJson = String.format("""
        {
            "salonName": "Amazing Barber",
            "category": "Hair and Beauty",
            "city": "Bialystok",
            "zipCode": "00-001",
            "street": "Main Street",
            "number": "123",
            "email": "someEmail@some.pl",
            "code": "%s"
        }
        """.trim(), codeDto.code());
        // when
        ResultActions performCreateSalon = addSalon(token, salonRequestJson);
        // then
        String createdSalonJson = performCreateSalon.andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        SalonFacadeResponseDto salonCreatedResponseDto = objectMapper.readValue(createdSalonJson, SalonFacadeResponseDto.class);
        assertAll(
                () -> assertThat(salonCreatedResponseDto.message()).isEqualTo("success"),
                () -> assertThat(salonCreatedResponseDto.salonId()).isEqualTo(1)
        );


//      step 6: owner made Patch /salon/add-opening-hours with valid hours and system returned OK(200)
        // given
        String salonOpeningHours = readJsonFromFile("salonOpeningHours.json");
        // when
        ResultActions performAddOpeningHours = addOpeningHours(token, salonOpeningHours);
        // then
        String addOpeningHoursJson = performAddOpeningHours.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        SalonFacadeResponseDto salonAddHoursResponseDto = objectMapper.readValue(addOpeningHoursJson, SalonFacadeResponseDto.class);
        assertThat(salonAddHoursResponseDto.message()).isEqualTo("success");


//      step 7: owner made POST /salon/1/employee with employee details and system added the employee, returning OK(200)
        // given
        String employeeDateAndAvailability = readJsonFromFile("employeeData.json");
        // when
        ResultActions performAddEmployee = addEmployee(token, employeeDateAndAvailability);
        // then
        String addEmployeeJson = performAddEmployee.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CreateEmployeeResponseDto salonEmployeeResponse = objectMapper.readValue(addEmployeeJson, CreateEmployeeResponseDto.class);
        assertAll(
                () -> assertThat(salonEmployeeResponse.employeePassword()).isNotNull(),
                () -> assertThat(salonEmployeeResponse.employeeEmail()).isEqualTo("Kot@example.com"),
                () -> assertThat(salonEmployeeResponse.message()).isEqualTo("success")
        );

    //  step 8: owner made POST /offer with offer details and system created the offer, returning Created(201) with offerId=1
        // given
        String offer = """
                {
                    "name": "Haircut",
                    "description": "A stylish haircut and grooming session.",
                    "price": 49.99,
                    "duration": "00:30:00",
                    "salonId": 1
                }
                """.trim();
        // when
        ResultActions performAddOffer = addOffer(token, offer);
        // then
        String offerJson = performAddOffer.andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        OfferFacadeResponse offerFacadeResponse = objectMapper.readValue(offerJson, OfferFacadeResponse.class);
        assertAll(
                () -> assertThat(offerFacadeResponse.OfferId()).isEqualTo(1),
                () -> assertThat(offerFacadeResponse.message()).isEqualTo("success")
        );

        //  step 9: owner made POST /offers with offer details and system created the offer, returning Created(201) with offerId=1
        // given
        String offer2 = """
                {
                    "name": "Haircut && Beard",
                    "description": "A stylish haircut and grooming session.",
                    "price": 99.99,
                    "duration": "01:00:00",
                    "salonId": 1
                }
                """.trim();
        // when
        ResultActions performAddOffer2 = addOffer(token, offer2);
        // then
        String offerJson2 = performAddOffer2.andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        OfferFacadeResponse offerFacadeResponse2 = objectMapper.readValue(offerJson2, OfferFacadeResponse.class);
        assertAll(
                () -> assertThat(offerFacadeResponse2.OfferId()).isEqualTo(2),
                () -> assertThat(offerFacadeResponse2.message()).isEqualTo("success")
        );


//      step 9: owner assign offer 1 to employee with id 1 make PATCH to /employees/add-offer, returning OK(200)
        // given
        String assignOffer = """
                {
                  "offerId": 1,
                  "employeeId": 1
                }
                """.trim();
        // when
        ResultActions performAssignOffer = addOfferToEmployee(token, assignOffer);
        // then
        String assignOfferJson = performAssignOffer.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        EmployeeFacadeResponseDto employeeFacadeResponse = objectMapper.readValue(assignOfferJson, EmployeeFacadeResponseDto.class);
        assertAll(
                () -> assertThat(employeeFacadeResponse.employeeId()).isEqualTo(1),
                () -> assertThat(employeeFacadeResponse.message()).isEqualTo("success")
        );


//      step 10: owner assign offer 1 to employee with id 1 make PATCH to /employees/add-offer, returning OK(200)
        // given
        String assignOffer2 = """
                {
                  "offerId": 2,
                  "employeeId": 1
                }
                """.trim();
        // when
        ResultActions performAssignOffer2 = addOfferToEmployee(token, assignOffer2);
        // then
        String assignOfferJson2 = performAssignOffer2.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        EmployeeFacadeResponseDto employeeFacadeResponse2 = objectMapper.readValue(assignOfferJson2, EmployeeFacadeResponseDto.class);
        assertAll(
                () -> assertThat(employeeFacadeResponse2.employeeId()).isEqualTo(1),
                () -> assertThat(employeeFacadeResponse2.message()).isEqualTo("success")
        );

//###########################################################USER#########################################################################//
//      step 11: user made GET /salons and see 1 salon with name Amazing Barber with status OK(200)
        // given && when
        String jsonSalons = mockMvc.perform(get("/salons")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn()
                .getResponse()
                .getContentAsString();

        List<SalonWithIdDto> salons = objectMapper.readValue(jsonSalons, new TypeReference<>() {
        });
        //  then
        assertAll(
                () -> assertThat(salons.get(0).salonName()).isEqualTo("Amazing Barber"),
                () -> assertThat(salons.get(0).category()).isEqualTo("Hair and Beauty")
        );

//      step 12: user made GET /salons/1 and get allInformation about salon
        // given && when
        String jsonSalonWithId1 = mockMvc.perform(get("/salons/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn()
                .getResponse()
                .getContentAsString();

        SalonWithIdDto salon1 = objectMapper.readValue(jsonSalonWithId1, SalonWithIdDto.class);
        //  then
        assertAll(
                () -> assertThat(salon1.salonName()).isEqualTo("Amazing Barber"),
                () -> assertThat(salon1.category()).isEqualTo("Hair and Beauty")
       );
//     step 13: user choose offer and employee GET /employee/available-dates on 2024-12-31 with employee id 1 and offer 1, returning OK(200)
        // given
        String date = "2025-12-27";
        String employeeId = "1";
        String offerId = "1";

        // when
        ResultActions performGetAvailableDate = mockMvc.perform(get("/employee/available-dates")
                .param("date", date)
                .param("employeeId", employeeId)
                .param("offerId", offerId)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        String availableTermJson = performGetAvailableDate.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AvailableTermDto> availableTermDtos =  objectMapper.readValue(availableTermJson, new TypeReference<>() {
        });

        assertAll(
                () -> assertThat(availableTermDtos.size()).isGreaterThan(2)
        );


//     step 14: user make reservation POST /reservation 2024-12-31, returning CREATED(201)
        // given
        String reservationDate = """
                {
                    "employeeId": 1,
                    "offerId": 1,
                    "salonId": 1,
                    "reservationDateTime": "2026-12-31T13:00:00",
                    "userEmail": "szymon.b4310xxx@gmail.com"
                }
                """.trim();
        // when
        ResultActions performReservation = mockMvc.perform(post("/reservation")
                .content(reservationDate)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        // then
        String reservationJson = performReservation.andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReservationFacadeResponse reservationFacadeResponse = objectMapper.readValue(reservationJson, ReservationFacadeResponse.class);
        String password = reservationFacadeResponse.password();
        assertAll(
                () -> assertTrue(reservationFacadeResponse.isSuccess()),
                () -> assertThat(reservationFacadeResponse.message()).isEqualTo("success"),
                () -> assertThat(password).isNotNull()
        );
//     step 15: user tried to get JWT token by requesting POST /token with email=szymon.b4310xxxxxx@gmail.com", password=?? and system returned OK(200)
        // given
        String userAndPassword = String.format("""
                {
                    "email": "szymon.b4310xxx@gmail.com",
                    "password": "%s"
                }
                """, password).trim();
        // when

        ResultActions performGetToken = mockMvc.perform(post("/token")
                .content(userAndPassword)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        String tokenJson = performGetToken.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JwtResponseDto userJwtResponse = objectMapper.readValue(tokenJson, JwtResponseDto.class);
        String userToken = userJwtResponse.token();
        assertThat(userToken).isNotNull();


//      step 16: user make GET /reservation with body email, returning OK(200)
        // given && when
        ResultActions performUserReservation = mockMvc.perform(get("/reservations")
                .header("Authorization", "Bearer " + userToken)
                .param("email", "szymon.b4310xxx@gmail.com")
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        //then
        String userReservationJson = performUserReservation.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserReservationDataDto> userReservationDtoList= objectMapper.readValue(userReservationJson, new TypeReference<>() {
        });
        UserReservationDataDto userReservationDto = userReservationDtoList.get(0);

        assertAll(
                () -> assertThat(userReservationDto.offerName()).isEqualTo("Haircut"),
                () -> assertThat(userReservationDto.employeeName()).isEqualTo("Seba"),
                () -> assertThat(userReservationDto.salonName()).isEqualTo("Amazing Barber"),
                () -> assertThat(userReservationDto.reservationDateTime()).isEqualTo("2026-12-31T13:00:00")
        );

//      step 17: user make PATCH /reservation and change the date of reservation to 2024-12-31T14:00:00
        // given
        String reservationChange = """
                {
                  "reservationId": 1,
                  "userEmail": "szymon.b4310xxx@gmail.com",
                  "newReservationDate": "2026-12-31T14:00:00"
                }
                """.trim();

        // when
        ResultActions performChangeReservationTime = mockMvc.perform(patch("/reservation")
                .header("Authorization", "Bearer " + userToken)
                .content(reservationChange)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        String changeReservationJson = performChangeReservationTime.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserReservationDto reservationWithChangedData = objectMapper.readValue(changeReservationJson, UserReservationDto.class);

        assertThat(reservationWithChangedData.reservationDateTime()).isEqualTo("2026-12-31T14:00:00");


//      step 18: user make DELETE /reservation and delete to reservation
        // given
        String reservationDelete = """
                {
                  "reservationId": 1,
                  "userEmail": "szymon.b4310xxx@gmail.com"
                }
                """.trim();

        // when
        ResultActions performDeleteReservation = mockMvc.perform(delete("/reservation")
                .header("Authorization", "Bearer " + userToken)
                .content(reservationDelete)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        String deleteReservationJson = performDeleteReservation.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReservationFacadeResponse deleteReservation = objectMapper.readValue(deleteReservationJson, ReservationFacadeResponse.class);

        assertAll(
                () -> assertThat(deleteReservation.message()).isEqualTo("success"),
                () -> assertTrue(deleteReservation.isSuccess())
        );
    }

    @NotNull
    private ResultActions addOfferToEmployee(String token, String assignOffer) throws Exception {
        return mockMvc.perform(patch("/employees/add-offer")
                .header("Authorization", "Bearer " + token)
                .content(assignOffer)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @NotNull
    private ResultActions addEmployee(String token, String employeeDateAndAvailability) throws Exception {
        return mockMvc.perform(post("/salon/1/employee")
                .header("Authorization", "Bearer " + token)
                .content(employeeDateAndAvailability)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @NotNull
    private ResultActions addOpeningHours(String token, String salonOpeningHours) throws Exception {
        return mockMvc.perform(patch("/salon/add-opening-hours")
                .header("Authorization", "Bearer " + token)
                .content(salonOpeningHours)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @NotNull
    private ResultActions addSalon(String token, String salonRequestJson) throws Exception {
        return mockMvc.perform(post("/salon")
                .header("Authorization", "Bearer " + token)
                .content(salonRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @NotNull
    private ResultActions addOffer(String token, String createOffer) throws Exception {
        return mockMvc.perform(post("/offer")
                .header("Authorization", "Bearer " + token)
                .content(createOffer)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

}
