package in.controller;

import auth.AuthContext;
import auth.AuthContextFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.User;
import in.request.MetricRecordRequest;
import in.request.MetricRecordRequestList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.MetricRecordService;
import util.MockServletInputStream;
import util.MockServletOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricRecordServletTest {

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
    private MetricRecordServlet metricRecordServlet;

    @BeforeEach
    public void setUp() throws ServletException {
        when(request.getSession()).thenReturn(session);
        when(servletContext.getAttribute("metricRecordService")).thenReturn(metricRecordService);
        when(servletContext.getAttribute("objectMapper")).thenReturn(objectMapper);
        when(servletContext.getAttribute("authContextFactory")).thenReturn(authContextFactory);

        metricRecordServlet.init(servletConfig);
        when(metricRecordServlet.getServletContext()).thenReturn(servletContext);

    }

    @Test
    public void testGetAllUserMetrics() throws Exception {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1, metrics, LocalDate.now(), user);
        List<MetricRecord> metricRecords = List.of(metricRecord);

        when(authContextFactory.getAuthContextForUser(any())).thenReturn(authContext);
        when(authContext.getCurrentUsername()).thenReturn(user.getUsername());
        when(metricRecordService.getUserMetrics(anyString())).thenReturn(metricRecords);
        when(response.getOutputStream()).thenReturn(new MockServletOutputStream());

        metricRecordServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testAddMetrics() throws Exception {
        Metric metric = new Metric(1, "electricity");
        MetricRecordRequest metricRecordRequest = new MetricRecordRequest(metric, 123);
        MetricRecordRequestList recordRequestList = new MetricRecordRequestList(List.of(metricRecordRequest));

        when(request.getInputStream()).thenReturn(new MockServletInputStream(objectMapper.writeValueAsBytes(recordRequestList)));

        when(authContextFactory.getAuthContextForUser(any())).thenReturn(authContext);
        when(authContext.getCurrentUsername()).thenReturn("username");
        when(metricRecordService.addNewMonthlyMetric(anyString(), anyMap())).thenReturn(true);

        metricRecordServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }
}