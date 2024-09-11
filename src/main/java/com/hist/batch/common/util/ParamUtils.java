package com.hist.batch.common.util;

import java.util.Map;
import java.util.Objects;

import org.springframework.batch.core.JobParametersBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParamUtils {

	private static ObjectMapper objectMapper;

	public static JobParametersBuilder bodyToParam(JobParametersBuilder jobParam, String requestBody) {
		if(objectMapper == null) objectMapper = new ObjectMapper();

		if (!Objects.isNull(requestBody)) {
			try {
				Map<?, ?> paramMap = objectMapper.readValue(requestBody, Map.class);
				paramMap.forEach((key, value) -> jobParam.addString((String)key, String.valueOf(value)));
			} catch (JsonMappingException e) {
				log.error("addReqBodyToParam error :" + e.getMessage());
			} catch (JsonProcessingException e) {
				log.error("addReqBodyToParam error :" + e.getMessage());
			}
		}

		return jobParam;
	}
}
