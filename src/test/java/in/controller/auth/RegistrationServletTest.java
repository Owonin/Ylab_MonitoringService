package in.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.request.UserCredentialsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.UserService;
import util.MockServletInputStream;
import util.MockServletOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServletTest {

    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletConfig servletConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RegistrationServlet registrationServlet;

    @BeforeEach
    public void setUp() throws ServletException {
        when(servletContext.getAttribute("userService")).thenReturn(userService);
        when(servletContext.getAttribute("objectMapper")).thenReturn(objectMapper);
        registrationServlet.init(servletConfig);
        when(registrationServlet.getServletContext()).thenReturn(servletContext);

    }

    @Test
    public void testValidRegistration() throws Exception {
        UserCredentialsRequest userCredentials = new UserCredentialsRequest("validUsername", "validPassword");

        when(request.getInputStream()).thenReturn(new MockServletInputStream(objectMapper.writeValueAsBytes(userCredentials)));

        registrationServlet.doPost(request, response);

        verify(userService).registrateUser(eq("validUsername"), eq("validPassword"), any());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    public void testInvalidRegistration() throws Exception {
        UserCredentialsRequest userCredentials = new UserCredentialsRequest("invalid", "short");

        when(request.getInputStream()).thenReturn(new MockServletInputStream(objectMapper.writeValueAsBytes(userCredentials)));

        when(response.getOutputStream()).thenReturn(new MockServletOutputStream());

        registrationServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}