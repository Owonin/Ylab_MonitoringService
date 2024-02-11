package domain.mapper;

import domain.model.Metric;
import domain.model.MetricRecord;
import domain.model.User;
import org.junit.jupiter.api.Test;
import out.dto.MetricRecordDto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MetricRecordMapperTest {
//todo
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
    }
}