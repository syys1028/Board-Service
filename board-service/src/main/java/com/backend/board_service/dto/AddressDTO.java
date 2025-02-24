package com.backend.board_service.dto;

public class AddressDTO {
    private String city;
    private String street;
    private String zipcode;

    public AddressDTO(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }
}
