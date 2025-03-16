package com.backend.board_service.entity;

import com.backend.board_service.dto.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {      // 주소 embadded 타입
    private Long address_id;
    private String city;
    private String street;
    private String zipcode;

    public static Address fromDTO(AddressDTO dto) {
        return new Address(null, dto.getCity(), dto.getStreet(), dto.getZipcode());
    }
}
