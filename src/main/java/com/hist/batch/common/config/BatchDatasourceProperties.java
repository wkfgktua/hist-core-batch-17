package com.hist.batch.common.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "spring")
public class BatchDatasourceProperties {
	private Map<String, String> batchDatasource;
}