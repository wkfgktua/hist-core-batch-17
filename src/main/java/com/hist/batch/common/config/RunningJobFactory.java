package com.hist.batch.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;

public class RunningJobFactory {
	private static final List<JobExecution> runningJobList = new ArrayList<>();

	public static int addJob(JobExecution je) {
		runningJobList.add(je);
		return runningJobList.size();
	}

	public static int removeJob(JobExecution je) {
		runningJobList.remove(je);
		return runningJobList.size();
	}

	public static List<JobExecution> getRunningJob() {
		return runningJobList;
	}

	public static int getSize() {
		return runningJobList.size();
	}
}
