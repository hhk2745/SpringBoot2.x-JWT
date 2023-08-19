package me.silvernine.tutorial.service;

import lombok.AllArgsConstructor;
import me.silvernine.tutorial.dto.UserDto;
import me.silvernine.tutorial.entity.Authority;
import me.silvernine.tutorial.entity.User;
import me.silvernine.tutorial.repository.UserRepository;
import me.silvernine.tutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 1. 이미 같은 username으로 가입된 유저가 있는 지 확인
     * 2. UserDto 객체의 정보들을 기반으로 권한 객체와 유저 객체를 생성하여 Database에 저장
     */
    @Transactional
    public User signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * username을 파라미터로 받아 해당 유저의 정보 및 권한 정보를 리턴합니다.
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    /**
     * SecurityUtil의 getCurrentUsername() 메소드가 리턴하는 username의 유저 및 권한 정보를 리턴합니다.
     */
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        Function<String, Optional<? extends User>> findOneWithAuthoritiesByUsername = userRepository::findOneWithAuthoritiesByUsername;
        return SecurityUtil.getCurrentUsername().flatMap(findOneWithAuthoritiesByUsername);
    }


}
