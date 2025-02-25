package com.backend.board_service.entity;

import com.backend.board_service.dto.AddressDTO;

public class Address {      // 주소 embadded 타입
    private Long address_id;
    private String city;
    private String street;
    private String zipcode;

    public Address(Long address_id, String city, String street, String zipcode) {
        this.address_id = address_id;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public static Address fromDTO(AddressDTO dto) {
        return new Address(null, dto.getCity(), dto.getStreet(), dto.getZipcode());
    }

    public Long getAddress_id() {
        return address_id;
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
