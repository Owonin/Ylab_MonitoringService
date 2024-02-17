package domain.mapper;

import domain.model.Metric;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import out.dto.MetricDto;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface MetricMapper {
    MetricMapper INSTANCE = Mappers.getMapper(MetricMapper.class);

    Metric metricDtoToMetric(MetricDto metricDto);

    MetricDto metricToMetricDto(Metric metric);

    default Map<Metric, Integer> mapMetricDtoToMetricMap(Map<MetricDto, Integer> metricDtoMap) {
        Map<Metric, Integer> metricMap = new HashMap<>();
        for (Map.Entry<MetricDto, Integer> entry : metricDtoMap.entrySet()) {
            Metric metric = metricDtoToMetric(entry.getKey());
            metricMap.put(metric, entry.getValue());
        }
        return metricMap;
    }
}
