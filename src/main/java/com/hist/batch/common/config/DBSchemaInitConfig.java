package com.hist.batch.common.config;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DBSchemaInitConfig {

	private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DBSchemaInitConfig(@BatchDataSource DataSource dataSource) {
    	this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Value("${batch.sql.init.schema-locations:classpath:schema.sql}")
	private String schemaPath;


	@Bean
	@DependsOnDatabaseInitialization
	@ConditionalOnProperty(prefix = "batch.sql.init", name = "enabled", havingValue = "true", matchIfMissing = false)
	void sqlInitEnabled() {

		String currentSchema = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
		String sql = "SELECT COUNT(1) CNT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = 'MULTI_SYNC'";

		try {
			if(jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{currentSchema}) < 1) {
				ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

				if(!StringUtils.isBlank(schemaPath)) {
					populator.addScripts(new ClassPathResource(schemaPath.replace("classpath:", "")));
				}

				populator.execute(this.dataSource);
			};
		} catch (Exception e) {
			log.error("selectIsInformationSchema Error : {}", e.getMessage());
		}
	}

	@Bean
	@ConditionalOnProperty(prefix = "batch.sql.init", name = "enabled", havingValue = "false", matchIfMissing = true)
	void sqlInitDisabled() {
	}
}
