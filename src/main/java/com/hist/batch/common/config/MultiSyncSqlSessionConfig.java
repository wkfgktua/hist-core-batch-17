package com.hist.batch.common.config;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @MultiSyncSqlSessionConfig config
 * @author dlehdusmusic@histmate.co.kr
 */
@Configuration
public class MultiSyncSqlSessionConfig {

	@Value("${spring.multiSync.mapper:classpath:mapper/common/multiSync/MultiSyncMapper.xml}")
	private String multiSyncMapper;

	@Bean("MultiSyncSqlSessionFactory")
	public SqlSessionFactoryBean sqlSessionFactoryBean(@BatchDataSource DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource);
		sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(multiSyncMapper));
		return sqlSessionFactory;
	}
}