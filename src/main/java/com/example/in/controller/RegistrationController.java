package com.example.in.controller;

import com.example.domain.model.Role;
import com.example.in.request.UserCredentialsRequest;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @Operation(
            summary = "Контроллер регистрации",
            description = "Регестрирует пользователя в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная регистрация",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный тип введенных данных",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден в системе",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> registrateUser(@RequestBody UserCredentialsRequest userCredentials, HttpSession session) {
        if (isValid(userCredentials)) {
            userService.registrateUser(userCredentials.getUsername(), userCredentials.getPassword(), Set.of(Role.USER));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Проверьте правильность введенных данных");
        }
    }

    public boolean isValid(UserCredentialsRequest userCredentialsRequest) {
        String username = userCredentialsRequest.getUsername();
        String password = userCredentialsRequest.getPassword();

        return username != null && username.length() <= 255 && username.length() >= 6
                && password != null && password.length() <= 255 && password.length() >= 6;
    }
}
