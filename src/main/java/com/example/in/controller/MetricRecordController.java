package com.example.in.controller;


import com.example.auth.AuthContext;
import com.example.auth.AuthContextFactory;
import com.example.domain.mapper.MetricRecordMapper;
import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.in.request.MetricRecordRequest;
import com.example.in.request.MetricRecordRequestList;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.out.dto.MetricRecordDto;
import com.example.service.MetricRecordService;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metrics")
public class MetricRecordController {

    private final MetricRecordService metricRecordService;
    private final AuthContextFactory authContextFactory;

    /**
     * Получение метрик пользователя
     */
    @GetMapping(value = "/last", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getMetricsByMonth(@RequestParam(required = false) String month,
                                                    @RequestParam(required = false) String year,
                                                    HttpSession session) throws AuthenticationException {
        AuthContext authContext = authContextFactory.getAuthContextForUser(session.getId());


        if (month == null && year == null) {
            MetricRecord metricRecord = metricRecordService.getLastMetricRecord(authContext.getCurrentUsername());
            MetricRecordDto metricRecordDto = MetricRecordMapper.INSTANCE.metricRecordToMetricRecordDto(metricRecord);
            return ResponseEntity.ok().body(metricRecordDto);
        } else if (month != null && year != null) {
            MetricRecord metricRecord = metricRecordService.getMetricRecordByMonth(authContext.getCurrentUsername(),
                    Integer.parseInt(month), Integer.parseInt(year));
            MetricRecordDto metricRecordDto = MetricRecordMapper.INSTANCE.metricRecordToMetricRecordDto(metricRecord);
            return ResponseEntity.ok().body(metricRecordDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Добавление метрики пользователя
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewMetricRecord(@RequestBody MetricRecordRequestList recordRequestList,
                                                     HttpSession session) {

        AuthContext authContext = authContextFactory.getAuthContextForUser(session.getId());

        Map<Metric, Integer> metricMap = new HashMap<>();

        if (isValid(recordRequestList)) {
            for (MetricRecordRequest recordRequest : recordRequestList.getMetrics()) {
                metricMap.put(recordRequest.getMetric(), recordRequest.getValue());
            }
        } else {
            return ResponseEntity.badRequest().body("Проверьте правильность введенных данных");
        }

        if (metricRecordService.addNewMonthlyMetric(authContext.getCurrentUsername(), metricMap)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Данные за этот месяц уже введены");
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllUserMetricRecords(HttpSession session) {

        AuthContext authContext = authContextFactory.getAuthContextForUser(session.getId());

        List<MetricRecord> userMetrics = metricRecordService.getUserMetrics(authContext.getCurrentUsername());
        List<MetricRecordDto> userMetricsDto = userMetrics.stream()
                .map(MetricRecordMapper.INSTANCE::metricRecordToMetricRecordDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(userMetricsDto);
    }

    public boolean isValid(MetricRecordRequestList metricRecordRequestList) {
        List<MetricRecordRequest> metrics = metricRecordRequestList.getMetrics();

        return metrics != null && metrics.stream()
                .allMatch(metric -> (metric.getMetric() != null && metric.getValue() != null));
    }
}