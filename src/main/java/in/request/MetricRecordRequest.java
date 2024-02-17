package in.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import domain.model.Metric;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricRecordRequest {
    private Metric metric;
    private Integer value;
}
