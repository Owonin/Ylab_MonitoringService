package domain.mapper;

import domain.model.MetricRecord;
import domain.model.Role;
import domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import out.dto.MetricRecordDto;
import out.dto.UserDto;

import java.util.HashSet;
import java.util.Set;

@Mapper
public interface MetricRecordMapper {
    MetricRecordMapper INSTANCE = Mappers.getMapper(MetricRecordMapper.class);

    MetricRecord metricRecordDtoToMetricRecord(MetricRecordDto metricRecordDto);

    MetricRecordDto metricRecordToMetricRecordDto(MetricRecord metricRecord);
}
