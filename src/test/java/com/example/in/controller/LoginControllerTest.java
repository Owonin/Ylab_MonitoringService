package com.example.in.controller;

import com.example.auth.AuthContext;
import com.example.auth.AuthContextFactory;
import com.example.in.request.UserCredentialsRequest;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private UserService userService;

    @Mock
    private AuthContextFactory authContextFactory;

    @Mock
    private AuthContext authContext;

    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    void testLoginWithValidCredentials() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        UserCredentialsRequest userCredentials = new UserCredentialsRequest(username, password);
        MockHttpSession session = new MockHttpSession();
        when(authContextFactory.getAuthContextForUser(session.getId())).thenReturn(authContext);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userCredentials))
                        .session(session))
                .andExpect(status().isOk());

        verify(userService, times(1)).login(anyString(), anyString(), any());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        String username = "testUser";
        UserCredentialsRequest userCredentials = new UserCredentialsRequest("short", "pwd");
        MockHttpSession session = new MockHttpSession();

        when(authContextFactory.getAuthContextForUser(session.getId())).thenReturn(authContext);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userCredentials))
                        .session(session))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).login(anyString(), anyString(), any());
    }

}