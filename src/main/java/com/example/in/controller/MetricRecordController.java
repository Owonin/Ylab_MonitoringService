package com.example.in.controller;


import com.example.auth.AuthContext;
import com.example.auth.AuthContextFactory;
import com.example.domain.mapper.MetricRecordMapper;
import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.in.request.MetricRecordRequest;
import com.example.in.request.MetricRecordRequestList;
import com.example.out.dto.MetricRecordDto;
import com.example.service.MetricRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(
            summary = "Получение запрашиваемой метрики пользователя",
            description = "Получение актуальной или датированной метрики пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MetricRecordDto.class)
                    )}),
            @ApiResponse(responseCode = "400", description = "Неверный тип введенных данных",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Ошибка аунтификации",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Запрашиваемые данные не найдены",
                    content = @Content)
    })
    @GetMapping(value = "/last", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getMetricsByMonth(@RequestParam(required = false, name = "month") String month,
                                                    @RequestParam(required = false, name = "year") String year,
                                                    HttpSession session) {
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
    @Operation(
            summary = "Добавление метрики"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный тип введенных данных",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Ошибка аунтификации",
                    content = @Content),
    })
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

    @Operation(
            summary = "Получение метрик пользователя",
            description = "Получение всех метрик пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MetricRecordDto.class)
                    )}),
            @ApiResponse(responseCode = "400", description = "Неверный тип введенных данных",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Ошибка аунтификации",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Запрашиваемые данные не найдены",
                    content = @Content)
    })
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