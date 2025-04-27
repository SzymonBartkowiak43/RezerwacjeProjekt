package com.example.systemrezerwacji.domain.salonmodule.dto;



public record CreateNewSalonDto(String salonName,
                                String category,
                                String city,
                                String zipCode,
                                String street,
                                String number,
                                String email,
                                String code){

}
