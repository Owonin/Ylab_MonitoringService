package in.controller.auth;

import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exception.NotFoundException;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final ObjectMapper objectMapper;

    public LoginServlet() {
        objectMapper = new ObjectMapper();
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
        HttpSession session = req.getSession();

        AuthContext authContext = AuthContextFactory.getAuthContextForUser(session.getId());

        UserCredentialsRequest userCredentials = objectMapper.readValue(req.getInputStream(), UserCredentialsRequest.class);

        ConfigReader configReader = ConfigReader.getInstance();
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider(
                configReader.getProperty("URL"),
                configReader.getProperty("USER"),
                configReader.getProperty("PASSWORD"));
        UserRepository userRepository = new JdbcUserRepository(dbConnectionProvider);
        UserService userService = new UserServiceImpl(userRepository, authContext);

        try {
            userService.login(userCredentials.getUsername(), userCredentials.getPassword());
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        } catch (AuthenticationException e) {
            resp.setStatus(418); //todo
            byte[] errorMessage = objectMapper.writeValueAsBytes(e.getMessage());
            resp.getOutputStream().write(errorMessage);
        }
    }
}
