package in.controller;

import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.MetricRecordService;
import util.MockServletOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MetricRecordByMonthServletTest {

    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletConfig servletConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private MetricRecordService metricRecordService;

    @Mock
    private AuthContext authContext;

    @Mock
    private AuthContextFactory authContextFactory;

    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private MetricRecordByMonthServlet metricRecordByMonthServlet;

    @BeforeEach
    public void setUp() throws ServletException {
        when(request.getSession()).thenReturn(session);
        when(servletContext.getAttribute("metricRecordService")).thenReturn(metricRecordService);
        when(servletContext.getAttribute("objectMapper")).thenReturn(objectMapper);
        when(servletContext.getAttribute("authContextFactory")).thenReturn(authContextFactory);

        metricRecordByMonthServlet.init(servletConfig);
        when(metricRecordByMonthServlet.getServletContext()).thenReturn(servletContext);

    }

    @Test
    public void testGetCurrentMetric() throws Exception {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1, metrics, LocalDate.now(), user);

        when(request.getParameter("month")).thenReturn(null);
        when(request.getParameter("year")).thenReturn(null);

        when(authContextFactory.getAuthContextForUser(any())).thenReturn(authContext);
        when(authContext.getCurrentUsername()).thenReturn(user.getUsername());
        when(metricRecordService.getLastMetricRecord(anyString())).thenReturn(metricRecord);
        when(response.getOutputStream()).thenReturn(new MockServletOutputStream());

        metricRecordByMonthServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testGetMetricByMonth() throws Exception {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1, metrics, LocalDate.of(2000, 11, 11), user);

        when(request.getParameter("month")).thenReturn(String.valueOf(11));
        when(request.getParameter("year")).thenReturn(String.valueOf(2000));


        when(authContextFactory.getAuthContextForUser(any())).thenReturn(authContext);
        when(authContext.getCurrentUsername()).thenReturn(user.getUsername());
        when(metricRecordService.getMetricRecordByMonth(anyString(), eq(11), eq(2000))).thenReturn(metricRecord);
        when(response.getOutputStream()).thenReturn(new MockServletOutputStream());

        metricRecordByMonthServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}