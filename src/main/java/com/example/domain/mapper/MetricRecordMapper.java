package com.example.domain.mapper;

import com.example.domain.model.MetricRecord;
import com.example.out.dto.MetricRecordDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MetricRecordMapper {
    MetricRecordMapper INSTANCE = Mappers.getMapper(MetricRecordMapper.class);

    MetricRecord metricRecordDtoToMetricRecord(MetricRecordDto metricRecordDto);

    MetricRecordDto metricRecordToMetricRecordDto(MetricRecord metricRecord);
}
