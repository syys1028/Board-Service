package com.backend.board_service.entity;

import com.backend.board_service.dto.user.AddressDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "addresses")
public class Address {      // 주소 embadded 타입
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long address_id;
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
