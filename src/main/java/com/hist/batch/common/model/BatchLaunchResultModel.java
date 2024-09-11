package com.hist.batch.common.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.batch.core.BatchStatus;

import lombok.Data;

/**
 * 배치 작업 결과를 표현하는 모델
 * @author dlehdusmusic@histmate.co.kr
 *
 */
@Data
@SuppressWarnings("serial")
public class BatchLaunchResultModel implements Serializable {

	private Long jobExecutionId;
	private String code;
	private String jobName;
	private String message;
	private String param;

	private long totalCnt;
	private long errorCnt;

	private BatchStatus status;
	private String exitCode;

	private Date startTime;
	private Date endTime;

	public BatchLaunchResultModel() {
		this.totalCnt = 0;
		this.errorCnt = 0;
	}

	public BatchLaunchResultModel(String code, String message) {
		this(null, null, code, message);
	}

	public BatchLaunchResultModel(String jobName, Long jobExecutionId, String code, String message) {
		this.jobName = jobName;
		this.jobExecutionId = jobExecutionId;
		this.code = code;
		this.message = message;
		this.totalCnt = 0;
		this.errorCnt = 0;
	}

	@Override
	public String toString() {
		return "BatchLaunchResultModel ["
				+ "jobName=" + jobName
				+ ", jobExecutionId=" + jobExecutionId
				+ ", code=" + code
				+ ", message=" + message
				+ ", param=" + param
				+ "]";
	}
}
