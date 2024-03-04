package com.example.in.controller;

import com.example.auth.AuthContext;
import com.example.auth.AuthContextFactory;
import com.example.in.request.UserCredentialsRequest;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AuthContextFactory authContextFactory;
    private final UserService userService;

    @Operation(
            summary = "Контроллер входа в аккаунт",
            description = "Авторизирует пользователя в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный тип данных",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден в системе",
                    content = @Content)
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody UserCredentialsRequest userCredentials, HttpSession session) {

        AuthContext authContext = authContextFactory.getAuthContextForUser(session.getId());

        if (isValid(userCredentials)) {
            userService.login(userCredentials.getUsername(), userCredentials.getPassword(), authContext);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Проверьте правильность введенных данных");
        }

    }

    public boolean isValid(UserCredentialsRequest userCredentialsRequest) {
        String username = userCredentialsRequest.getUsername();
        String password = userCredentialsRequest.getPassword();

        return username != null && username.length() <= 255 && username.length() >= 6
                && password != null && password.length() <= 255 && password.length() >= 6;
    }
}
