package com.backend.board_service.entity.user.dto;

import com.backend.board_service.entity.user.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class AddressDTO {
    private String city;
    private String street;
    private String zipcode;

    // Address → AddressDTO 변환 메서드
    public static AddressDTO fromEntity(Address address) {
        return new AddressDTO(address.getCity(), address.getStreet(), address.getZipcode());
    }
}
