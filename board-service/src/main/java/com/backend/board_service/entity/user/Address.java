package com.backend.board_service.entity.user;

import com.backend.board_service.entity.user.dto.AddressDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {      // 주소 embadded 타입
    private String city;
    private String street;
    private String zipcode;

    public static Address fromDTO(AddressDTO dto) {
        return Address.builder()
                .city(dto.getCity())
                .street(dto.getStreet())
                .zipcode(dto.getZipcode())
                .build();
    }
}
