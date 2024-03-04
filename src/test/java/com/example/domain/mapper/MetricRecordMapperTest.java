package com.example.domain.mapper;

import com.example.domain.model.Metric;
import com.example.domain.model.MetricRecord;
import com.example.domain.model.User;
import org.junit.jupiter.api.Test;
import com.example.out.dto.MetricRecordDto;
import com.example.out.dto.UserDto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetricRecordMapperTest {
    @Test
    void metricRecordDtoToMetricRecord() {
        User user = new User(1, "username");
        Map<Metric, Integer> metrics = new HashMap<>();
        metrics.put(new Metric(1, "hotwater"), 100);
        metrics.put(new Metric(2, "coldwater"), 100);
        MetricRecord metricRecord = new MetricRecord(1,metrics, LocalDate.of(2000, 11, 11), user);

        MetricRecordDto metricRecordDto = MetricRecordMapper.INSTANCE.metricRecordToMetricRecordDto(metricRecord);

        assertNotNull(metricRecordDto);
    }

    @Test
    void metricRecordToMetricRecordDto() {

        MetricRecordDto metricRecordDto = new MetricRecordDto();
        metricRecordDto.setMetricDate("2000-11-11");
        metricRecordDto.setMetrics(null);
        metricRecordDto.setId(1);
        metricRecordDto.setUser(new UserDto());

        MetricRecord metricRecord = MetricRecordMapper.INSTANCE.metricRecordDtoToMetricRecord(metricRecordDto);

        assertNotNull(metricRecord);
    }
}