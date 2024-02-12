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
import javax.servlet.http.HttpSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServletTest {

    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletConfig servletConfig;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LoginServlet loginServlet;

    @BeforeEach
    public void setUp() throws ServletException {
        when(request.getSession()).thenReturn(session);
        when(servletContext.getAttribute("userService")).thenReturn(userService);
        when(servletContext.getAttribute("objectMapper")).thenReturn(objectMapper);
        loginServlet.init(servletConfig);
        when(loginServlet.getServletContext()).thenReturn(servletContext);

    }

    @Test
    public void testValidLogin() throws Exception {
        UserCredentialsRequest userCredentials = new UserCredentialsRequest("validUsername", "validPassword");
        when(request.getInputStream()).thenReturn(new MockServletInputStream(objectMapper.writeValueAsBytes(userCredentials)));

        loginServlet.doPost(request, response);

        verify(userService).login(eq("validUsername"), eq("validPassword"), any());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testInvalidLogin() throws Exception {
        UserCredentialsRequest userCredentials = new UserCredentialsRequest("invalid", "short");
        when(request.getInputStream()).thenReturn(new MockServletInputStream(objectMapper.writeValueAsBytes(userCredentials)));

        when(response.getOutputStream()).thenReturn(new MockServletOutputStream());

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}