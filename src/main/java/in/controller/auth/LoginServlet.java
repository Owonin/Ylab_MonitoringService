package in.controller.auth;

import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exception.NotFoundException;
import in.request.UserCredentialsRequest;
import service.UserService;

import javax.naming.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * Вход в аккаунт
     *
     * @param req  an {@link HttpServletRequest} object that
     *             contains the request the client has made
     *             of the servlet
     * @param resp an {@link HttpServletResponse} object that
     *             contains the response the servlet sends
     *             to the client
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext servletContext = getServletContext();
        UserService userService = (UserService) servletContext.getAttribute("userService");
        ObjectMapper objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");

        HttpSession session = req.getSession();

        AuthContext authContext = AuthContextFactory.getAuthContextForUser(session.getId());

        UserCredentialsRequest userCredentials = objectMapper.readValue(req.getInputStream(), UserCredentialsRequest.class);

        try {
            if (userCredentials.isValid()) {
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
}