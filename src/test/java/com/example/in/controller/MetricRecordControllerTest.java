package com.example.in.controller;

import com.example.auth.AuthContext;
import com.example.auth.AuthContextFactory;
import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.domain.model.User;
import com.example.in.request.MetricRecordRequest;
import com.example.in.request.MetricRecordRequestList;
import com.example.service.MetricRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MetricRecordControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private MetricRecordService metricRecordService;

    @Mock
    private AuthContextFactory authContextFactory;

    @Mock
    private AuthContext authContext;

    @InjectMocks
    private MetricRecordController metricRecordController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(metricRecordController).build();
    }

    @Test
    void testGetMetricsByMonth() throws Exception {
        User user = new User(1, "username");

        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1, metrics, LocalDate.now(), user);

        String month = "1";
        String year = "2024";
        MockHttpSession session = new MockHttpSession();

        when(authContextFactory.getAuthContextForUser(session.getId())).thenReturn(authContext);
        when(authContext.getCurrentUsername()).thenReturn(user.getUsername());

        when(metricRecordService.getMetricRecordByMonth(eq(user.getUsername()), anyInt(), anyInt()))
                .thenReturn(metricRecord);

        mockMvc.perform(get("/metrics/last")
                        .param("month", month)
                        .param("year", year)
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUserMetricRecords() throws Exception {
        User user = new User(1, "username");

        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "electricity"), 100);
        metrics.put(new Metric(2, "water"), 100);
        MetricRecord metricRecord = new MetricRecord(1, metrics, LocalDate.now(), user);

        MockHttpSession session = new MockHttpSession();
        when(authContextFactory.getAuthContextForUser(session.getId())).thenReturn(authContext);
        when(authContext.getCurrentUsername()).thenReturn(user.getUsername());

        when(metricRecordService.getUserMetrics(user.getUsername())).thenReturn(List.of(metricRecord));

        mockMvc.perform(get("/metrics/")
                        .session(session))
                .andExpect(status().isOk());

    }

    @Test
    void testAddNewMetricRecord() throws Exception {

        MetricRecordRequestList requestList = new MetricRecordRequestList();
        MetricRecordRequest metricRequest = new MetricRecordRequest();
        metricRequest.setMetric(new Metric(1, "MetricName"));
        metricRequest.setValue(10);
        requestList.setMetrics(List.of(metricRequest));

        MockHttpSession session = new MockHttpSession();

        when(authContextFactory.getAuthContextForUser(session.getId())).thenReturn(authContext);
        when(authContext.getCurrentUsername()).thenReturn("username");

        when(metricRecordService.addNewMonthlyMetric(anyString(), anyMap())).thenReturn(true);

        mockMvc.perform(post("/metrics/")
                        .contentType(MediaType.APPLICATION_JSON.getMediaType())
                        .content(new ObjectMapper().writeValueAsString(requestList))
                        .session(session))
                .andExpect(status().isCreated());

    }

}