package in.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import domain.model.Role;
import domain.repository.UserRepository;
import domain.repository.jdbc.JdbcUserRepository;
import in.request.UserCredentialsRequest;
import service.UserService;
import service.impl.UserServiceImpl;
import util.ConfigReader;
import util.DBConnectionProvider;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    public RegistrationServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ConfigReader configReader = ConfigReader.getInstance();
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider(
                configReader.getProperty("URL"),
                configReader.getProperty("USER"),
                configReader.getProperty("PASSWORD"));
        UserRepository userRepository = new JdbcUserRepository(dbConnectionProvider);
        this.userService = new UserServiceImpl(userRepository, null);
    }

    /**
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


        UserCredentialsRequest userCredentials = objectMapper.readValue(req.getInputStream(), UserCredentialsRequest.class);

        try {
            if (userCredentials.isValid()) {
                userService.registrateUser(userCredentials.getUsername(), userCredentials.getPassword(), Set.of(Role.USER));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }else {
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
}
