package com.backend.board_service.controller;

import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.dto.UserUpdateDTO;
import com.backend.board_service.exception.UserNotFoundException;
import com.backend.board_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. 회원 가입 (POST /users)
    @PostMapping
    public ResponseEntity<Long> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        Long userId = userService.addUser(userRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    // 2. 회원 조회 (GET /users/{email})
    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("email") String email) {
        UserDTO user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 이메일의 사용자가 존재하지 않습니다."));
        return ResponseEntity.ok(user);
    }

    // 3. 회원 정보 수정 (PUT /users/{email})
    @PutMapping("/{email}")
    public ResponseEntity<Void> updateUser(@PathVariable("email") String email, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        Long userId = userService.getUserIdByEmail(email);  // id(pk)가져와서 사용
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        boolean updated = userService.updateUser(userId, userUpdateDTO);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // 4. 회원 삭제 (DELETE /users/{email})
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email) {
        Long userId = userService.getUserIdByEmail(email);  // id(pk)가져와서 사용
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
