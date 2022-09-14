package com.toeic.online.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toeic.online.domain.User;
import com.toeic.online.security.UserNotActivatedException;
import com.toeic.online.security.jwt.JWTFilter;
import com.toeic.online.security.jwt.JwtResponse;
import com.toeic.online.security.jwt.TokenProvider;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.AdminUserDTO;
import com.toeic.online.service.mapper.UserMapper;
import com.toeic.online.web.rest.vm.LoginVM;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserService userService;

    private final UserMapper userMapper;

    public UserJWTController(
        TokenProvider tokenProvider,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        UserService userService,
        UserMapper userMapper
    ) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());
            User user = userService.getUserWithAuthoritiesByLogin(loginVM.getUsername()).orElse(new User());
            AdminUserDTO userDTO = userMapper.userToAdminUserDTO(user);
            JwtResponse jwtResponse = new JwtResponse(jwt, userDTO);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return new ResponseEntity<>(jwtResponse, httpHeaders, HttpStatus.OK);
        } catch (UserNotActivatedException e) {
            throw new UserNotActivatedException(e.getMessage());
        } catch (InternalAuthenticationServiceException eux) {
            throw new UserNotActivatedException("Tài khoản bị khóa");
        } catch (AuthenticationException ex) {
            throw new UserNotActivatedException("Đăng nhập không thành công");
        }
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String jwttoken;
        private AdminUserDTO currentUser;

        JWTToken(String jwt, AdminUserDTO userDTO) {
            this.jwttoken = jwt;
            this.currentUser = userDTO;
        }

        @JsonProperty("jwttoken")
        String getJwttoken() {
            return jwttoken;
        }

        void setJwttoken(String jwttoken) {
            this.jwttoken = jwttoken;
        }

        AdminUserDTO getCurrentUser() {
            return currentUser;
        }

        void setCurrentUser(AdminUserDTO userDTO) {
            this.currentUser = userDTO;
        }
    }
}
