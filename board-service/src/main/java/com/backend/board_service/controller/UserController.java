package com.backend.board_service.controller;

import com.backend.board_service.dto.UserDTO;
import com.backend.board_service.dto.UserRegisterDTO;
import com.backend.board_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    public ResponseEntity<Long> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        Long userId = userService.addUser(userRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    // 2. 회원 조회 (GET /users/{email})
    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("email") String email) {
        Optional<UserDTO> user = userService.findUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); // 이거 자동으로 바꿔주는데 뭐지? ,,,
    }

    // 3. 회원 정보 수정 (PUT /users/{email})
    @PutMapping("/{email}")
    public ResponseEntity<Void> updateUser(@PathVariable("email") String email, @RequestBody UserRegisterDTO userDTO) {
        Long userId = userService.getUserIdByEmail(email);  // id(pk)가져와서 사용
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        boolean updated = userService.updateUser(userId, userDTO);
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
