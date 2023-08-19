package me.silvernine.tutorial.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.silvernine.tutorial.dto.UserDto;
import me.silvernine.tutorial.entity.User;
import me.silvernine.tutorial.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * permitAll를 설정했기 때문에 권한 없이 호출 가능
     */
    @PostMapping("/signup")
    public ResponseEntity<User> signup(
            @Valid @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userService.signup(userDto));
    }

    /**
     * 현재 Security Context에 저장되어 있는 인증 정보의 username을 기준으로 한 유저 정보 및 권한 정보를 리턴
     * &#064;PreAuthorize(“hasAnyRole(‘USER’,‘ADMIN’)”)  어노테이션을 이용해서 ROLE_USER, ROLE_ADMIN 권한 모두 호출 가능하게 설정합니다.
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<User> getMyUserInfo() {
        Optional<User> user = userService.getMyUserWithAuthorities();
        return ResponseEntity.of(user);
    }

    /**
     * username을 파라미터로 받아 해당 username의 유저 정보 및 권한 정보를 리턴
     * &#064;PreAuthorize(“hasAnyRole(‘ADMIN’)”) 어노테이션을 이용해서 ROLE_ADMIN 권한을 소유한 토큰만 호출할 수 있도록 설정합니다.
     */
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username).get());
    }
}

