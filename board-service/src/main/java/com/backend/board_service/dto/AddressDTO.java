package com.backend.board_service.dto;

import com.backend.board_service.entity.Address;

public class AddressDTO {
    private String city;
    private String street;
    private String zipcode;

    public AddressDTO(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // Address → AddressDTO 변환 메서드
    public static AddressDTO fromEntity(Address address) {
        return new AddressDTO(address.getCity(), address.getStreet(), address.getZipcode());
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
