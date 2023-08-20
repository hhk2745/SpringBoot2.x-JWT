package me.silvernine.tutorial.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.silvernine.tutorial.dto.LoginDto;
import me.silvernine.tutorial.dto.TokenDto;
import me.silvernine.tutorial.jwt.JwtFilter;
import me.silvernine.tutorial.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * username, password를 파라미터로 받아서 UsernamePasswordAuthenticationToken 객체를 생성합니다.
     * 해당 객체를 통해 **authenticate** 메소드 로직을 수행합니다. 이때 위에서 만들었던 **loadUserByUsername** 메소드가 수행되며 유저 정보를 조회해서 인증 정보를 생성하게 됩니다.
     * 해당 인증 정보를 JwtFilter 클래스의 **doFilter** 메소드와 유사하게 현재 실행중인 스레드 ( Security Context ) 에 저장합니다.
     * 또한 해당 인증 정보를 기반으로 TokenProvider의 **createToken** 메소드를 통해 jwt 토큰을 생성합니다.
     * 생성된 Token을 Response Header에 넣고, TokenDto 객체를 이용해 Reponse Body에도 넣어서 리턴합니다.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        AuthenticationManager object = authenticationManagerBuilder.getObject();
        // authenticate 메소드 실행하면 loadUserByUsername 호출 됨
        Authentication authentication = object.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);
        String headerValue = "Bearer " + jwt;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, headerValue);
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}
