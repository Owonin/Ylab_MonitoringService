package in.controller.servlets.auth;

/* dfd
import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exception.NotFoundException;
import in.request.UserCredentialsRequest;
import service.UserService;

import javax.naming.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext servletContext = getServletContext();
        UserService userService = (UserService) servletContext.getAttribute("userService");
        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");
        AuthContextFactory authContextFactory = (AuthContextFactory) servletContext.getAttribute("authContextFactory");

        HttpSession session = req.getSession();

        AuthContext authContext = authContextFactory.getAuthContextForUser(session.getId());

        UserCredentialsRequest userCredentials = objectMapper.readValue(req.getInputStream(), UserCredentialsRequest.class);

        try {
            if (isValid(userCredentials)) {
                userService.login(userCredentials.getUsername(), userCredentials.getPassword(), authContext);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                byte[] errorMessage = objectMapper.writeValueAsBytes("Логин и пароль должны состоять от 6 до 225 символов");
                resp.getOutputStream().write(errorMessage);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        } catch (AuthenticationException e) {
            resp.setStatus(418);
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        }
    }

    public boolean isValid(UserCredentialsRequest userCredentialsRequest) {
        String username = userCredentialsRequest.getUsername();
        String password = userCredentialsRequest.getPassword();

        return username != null && username.length() <= 255 && username.length() >= 6
                && password != null && password.length() <= 255 && password.length() >= 6;
    }
}
*/