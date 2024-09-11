package com.hist.batch.common.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.hist.core.util.CommonUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	private BatchDatasourceProperties batchDatasourceProperties;

	@BatchDataSource
	@Bean("BatchDatasource")
	public DataSource dataSource() {
		Map<String, String> multiDSPropertiesMap = batchDatasourceProperties.getBatchDatasource();

		HikariConfig hconf = new HikariConfig(CommonUtils.toProperties(multiDSPropertiesMap));
		hconf.validate();

		return new HikariDataSource(hconf);
	}

	@Bean
	@Primary
	@Profile("rds")
	public PlatformTransactionManager getTransactionManager(DataSource ds) {
		return new DataSourceTransactionManager(ds);
	}
}