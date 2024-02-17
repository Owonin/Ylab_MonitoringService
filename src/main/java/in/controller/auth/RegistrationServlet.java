package in.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.model.Role;
import in.request.UserCredentialsRequest;
import service.UserService;

import javax.naming.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    /**
     * Регестрация пользователя
     *
     * @param req  an {@link HttpServletRequest} object that
     *             contains the request the client has made
     *             of the servlet
     * @param resp an {@link HttpServletResponse} object that
     *             contains the response the servlet sends
     *             to the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        UserService userService = (UserService) servletContext.getAttribute("userService");
        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");

        UserCredentialsRequest userCredentials = objectMapper.readValue(req.getInputStream(), UserCredentialsRequest.class);

        try {
            if (isValid(userCredentials)) {
                userService.registrateUser(userCredentials.getUsername(), userCredentials.getPassword(), Set.of(Role.USER));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                byte[] errorMessage = objectMapper.writeValueAsBytes("Логин и пароль должны состоять от 6 до 225 символов");
                resp.getOutputStream().write(errorMessage);
            }
        } catch (AuthenticationException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
