package in.controller;

import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import domain.exception.NotFoundException;
import domain.mapper.MetricRecordMapper;
import domain.model.MetricRecord;
import domain.repository.MetricRecordRepository;
import domain.repository.UserRepository;
import domain.repository.jdbc.JdbcMetricRecordRepository;
import domain.repository.jdbc.JdbcUserRepository;
import out.dto.MetricRecordDto;
import service.MetricRecordService;
import service.impl.MetricRecordServiceImpl;
import util.ConfigReader;
import util.DBConnectionProvider;
import util.ServletErrorHandler;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/metric_by_month")
public class MetricRecordByMonthServlet extends HttpServlet {

    private final ObjectMapper objectMapper;
    private final MetricRecordService metricRecordService;

    public MetricRecordByMonthServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ConfigReader configReader = ConfigReader.getInstance();
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider(
                configReader.getProperty("URL"),
                configReader.getProperty("USER"),
                configReader.getProperty("PASSWORD"));
        MetricRecordRepository metricRecordRepository = new JdbcMetricRecordRepository(dbConnectionProvider);
        UserRepository userRepository = new JdbcUserRepository(dbConnectionProvider);
        metricRecordService = new MetricRecordServiceImpl(metricRecordRepository, userRepository);
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        AuthContext authContext = AuthContextFactory.getAuthContextForUser(session.getId());

        String month = req.getParameter("month");
        String year = req.getParameter("year");

        try {
            if (month == null && year == null) {
                getLastMonthMetric(resp, authContext);
            } else if (month != null && year != null) {
                getMetricByDate(resp, authContext, month, year);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (AuthenticationException e) {
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_FORBIDDEN,objectMapper);
        } catch (NotFoundException e) {
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND,objectMapper);
        }
    }

    private void getMetricByDate(HttpServletResponse resp, AuthContext authContext, String month, String year)
            throws NotFoundException, AuthenticationException, IOException {
        MetricRecord metricRecord = metricRecordService.getMetricRecordByMonth(Integer.parseInt(month),
                Integer.parseInt(year),
                authContext.getCurrentUsername());
        MetricRecordDto metricRecordDto = MetricRecordMapper.INSTANCE.metricRecordToMetricRecordDto(metricRecord);
        byte[] message = objectMapper.writeValueAsBytes(metricRecordDto);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().write(message);
    }

    private void getLastMonthMetric(HttpServletResponse resp, AuthContext authContext)
            throws NotFoundException, AuthenticationException, IOException {
        MetricRecord metricRecord = metricRecordService.getLastMetricRecord(authContext.getCurrentUsername());
        MetricRecordDto metricRecordDto = MetricRecordMapper.INSTANCE.metricRecordToMetricRecordDto(metricRecord);
        byte[] message = objectMapper.writeValueAsBytes(metricRecordDto);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().write(message);
    }
}
