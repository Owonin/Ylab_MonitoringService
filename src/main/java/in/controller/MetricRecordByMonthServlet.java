package in.controller;

import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exception.NotFoundException;
import domain.mapper.MetricRecordMapper;
import domain.model.MetricRecord;
import out.dto.MetricRecordDto;
import service.MetricRecordService;
import util.ServletErrorHandler;

import javax.naming.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/metric_by_month")
public class MetricRecordByMonthServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private MetricRecordService metricRecordService;


    /**
     * Получение метрики по месяцу
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        metricRecordService = (MetricRecordService) servletContext.getAttribute("metricRecordService");
        objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");
        AuthContextFactory authContextFactory = (AuthContextFactory) servletContext.getAttribute("authContextFactory");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        AuthContext authContext = authContextFactory.getAuthContextForUser(session.getId());

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
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_FORBIDDEN, objectMapper);
        } catch (NotFoundException e) {
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND, objectMapper);
        }
    }

    /**
     * Получение метрики по дате
     *
     * @param resp        ответ сервлета
     * @param authContext Контекст авторизации
     * @param month       Месяц
     * @param year        Год
     * @throws NotFoundException
     * @throws AuthenticationException
     * @throws IOException
     */
    private void getMetricByDate(HttpServletResponse resp, AuthContext authContext, String month, String year)
            throws NotFoundException, AuthenticationException, IOException {
        MetricRecord metricRecord = metricRecordService.getMetricRecordByMonth(authContext.getCurrentUsername(),
                Integer.parseInt(month),
                Integer.parseInt(year));
        MetricRecordDto metricRecordDto = MetricRecordMapper.INSTANCE.metricRecordToMetricRecordDto(metricRecord);
        byte[] message = objectMapper.writeValueAsBytes(metricRecordDto);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().write(message);
    }

    /**
     * Получение актуальной метрики
     *
     * @param resp        ответ сервлета
     * @param authContext Контекст авторизации
     * @throws NotFoundException
     * @throws AuthenticationException
     * @throws IOException
     */
    private void getLastMonthMetric(HttpServletResponse resp, AuthContext authContext)
            throws NotFoundException, AuthenticationException, IOException {
        MetricRecord metricRecord = metricRecordService.getLastMetricRecord(authContext.getCurrentUsername());
        MetricRecordDto metricRecordDto = MetricRecordMapper.INSTANCE.metricRecordToMetricRecordDto(metricRecord);
        byte[] message = objectMapper.writeValueAsBytes(metricRecordDto);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().write(message);
    }
}
