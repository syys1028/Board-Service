package com.backend.board_service.entity.user;

import com.backend.board_service.entity.post.Post;
import com.backend.board_service.entity.user.dto.UserRegisterDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // 사용자 ID

    @Column(nullable = false, unique = true)
    private String email;                   // 사용자 이메일

    @Column(nullable = false)
    private String pw;                      // 사용자 비밀번호

    @Column(nullable = false, unique = true)
    private String nickname;                // 닉네임

    @Column(nullable = false)
    private String name;                    // 사용자 이름

    @Column(nullable = false)
    private String phone;                   // 전화번호

    @Column(nullable = false)
    private int age;                        // 나이

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;                  // 성별

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;        // 작성 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Embedded
    private Address address;                // 주소

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    // DTO -> User로 변환
    public static User fromRegisterDTO(UserRegisterDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .pw(dto.getPw())
                .age(dto.getAge())
                .gender(dto.getGender())
                .address(Address.fromDTO(dto.getAddressDTO()))
                .build();
    }
}
