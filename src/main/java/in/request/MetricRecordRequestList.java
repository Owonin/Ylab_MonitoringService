package in.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MetricRecordRequestList {
    List<MetricRecordRequest> metrics;
}
