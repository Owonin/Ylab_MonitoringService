package in.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricRecordRequestList {
    List<MetricRecordRequest> metrics;

    public boolean isValid() {
        return metrics != null && metrics.stream().allMatch(MetricRecordRequest::isValid);
    }
}
