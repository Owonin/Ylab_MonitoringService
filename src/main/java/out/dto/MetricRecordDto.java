package out.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MetricRecordDto {
    private Integer id;
    private Map<MetricDto, Integer> metrics;
    private String metricDate;
    private UserDto user;
}
