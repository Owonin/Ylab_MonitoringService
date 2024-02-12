package in.controller;

import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exception.NotFoundException;
import domain.mapper.MetricRecordMapper;
import domain.model.Metric;
import domain.model.MetricRecord;
import in.request.MetricRecordRequest;
import in.request.MetricRecordRequestList;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/metric_record")
public class MetricRecordServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private MetricRecordService metricRecordService;

    /**
     * Получение метрик пользователя
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        metricRecordService = (MetricRecordService) servletContext.getAttribute("MetricRecordService");
        objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        AuthContext authContext = AuthContextFactory.getAuthContextForUser(session.getId());

        try {
            List<MetricRecord> userMetrics = metricRecordService.getUserMetrics(authContext.getCurrentUsername());
            List<MetricRecordDto> userMetricsDto = userMetrics.stream()
                    .map(MetricRecordMapper.INSTANCE::metricRecordToMetricRecordDto)
                    .collect(Collectors.toList());
            byte[] message = objectMapper.writeValueAsBytes(userMetricsDto);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getOutputStream().write(message);
        } catch (NotFoundException e) {
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND, objectMapper);
        } catch (AuthenticationException e) {
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_FORBIDDEN, objectMapper);
        }
    }

    /**
     * Добавление метрики пользователя
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
        metricRecordService = (MetricRecordService) servletContext.getAttribute("MetricRecordService");
        objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();

        AuthContext authContext = AuthContextFactory.getAuthContextForUser(session.getId());

        MetricRecordRequestList recordRequestList = objectMapper.readValue(req.getInputStream(), MetricRecordRequestList.class);

        Map<Metric, Integer> metricMap = new HashMap<>();

        if (recordRequestList.isValid()) {
            for (MetricRecordRequest recordRequest : recordRequestList.getMetrics()) {
                metricMap.put(recordRequest.getMetric(), recordRequest.getValue());
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            byte[] message = objectMapper.writeValueAsBytes("Проверте правильност введенных данных");
            resp.getOutputStream().write(message);
        }

        try {
            if (metricRecordService.addNewMonthlyMetric(authContext.getCurrentUsername(), metricMap)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                byte[] message = objectMapper.writeValueAsBytes("Данные за этот месяц уже сохранены");
                resp.getOutputStream().write(message);
            }

        } catch (NotFoundException e) {
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_NOT_FOUND, objectMapper);
        } catch (AuthenticationException e) {
            ServletErrorHandler.handleErrorMessage(resp, e.getMessage(), HttpServletResponse.SC_FORBIDDEN, objectMapper);
        }
    }
}
