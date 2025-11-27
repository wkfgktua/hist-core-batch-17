package com.hist.batch.common.controller;

import java.util.List;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hist.batch.common.config.RunningJobFactory;
import com.hist.batch.common.log.BatchLogFactory;
import com.hist.batch.common.multiSync.MultiSyncFactory;

@Controller
@RequestMapping("/debug")
public class DebugController {

	@Autowired
	private JobExplorer jobExplorer;

	@GetMapping("/")
	public ResponseEntity<String> runningJobList() {
		String html = "BatchLog, MultiSync 모니터링.";

		Set<JobExecution> batchLogKeySet = BatchLogFactory.getKeySet();

		if (!batchLogKeySet.isEmpty()) {
			html += "<br/><br/>[BatchLog Data] <br/><br/>";
			for (JobExecution key : batchLogKeySet) {
				html += "key : " + key.getId();
				html += "<br/>";
			}
		}

		Set<JobExecution> multiSyncKeySet = MultiSyncFactory.getKeySet();

		if (!multiSyncKeySet.isEmpty()) {
			html += "<br/><br/>[MultiSync Data] <br/><br/>";
			for (JobExecution key : multiSyncKeySet) {
				html += "key : " + key.getId();
				html += "<br/>";
			}
		}

		if (RunningJobFactory.getSize() > 0) {
			html += "<br/><br/>[Running Job List] <br/><br/>";
			for (JobExecution je : RunningJobFactory.getRunningJob()) {
				html += "JobExecution : " + je.toString();
				html += "<br/>";
			}
		}

		return new ResponseEntity<String>(html, HttpStatus.OK);
	}

	@GetMapping("/getJobAllList")
	public ResponseEntity<String> getJobAllList() {
		String html = "Batch Job 전체 모니터링.";

		List<String> jobNames = jobExplorer.getJobNames();

		for (String jobName : jobNames) {
			Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);
			for (JobExecution jobExecution : jobExecutions) {
				if (jobExecution.isRunning()) {
					html += "<br/><br/>[Running Job]";
					html += "<br/>Job Execution ID: " + jobExecution.getId();
					html += "<br/>Job Name: " + jobName;
					html += "<br/>Status: " + jobExecution.getStatus();
					html += "<br/>Start Time: " + jobExecution.getStartTime();
					html += "<br/>-------------------------------------";
				}
			}
		}

		return new ResponseEntity<String>(html, HttpStatus.OK);
	}

	@GetMapping("/getJobList")
	public ResponseEntity<String> getJobList() {
		StringBuilder html = new StringBuilder("Batch Job 모니터링.");
	    List<JobExecution> jobs = RunningJobFactory.getRunningJob();

	    html.append("\n<br/>RunningJobFactory count :").append(jobs.size());
	    html.append("\n<br/>RunningJobFactory list :").append(jobs);
	    html.append("\n<br/>");

	    synchronized (jobs) {
	        for (JobExecution je : jobs) {
	            if (je == null) {
	                html.append("\n<br/>je is null !!");
	            } else {
	                html.append("\n<br/>job id : ").append(je.getId())
	                    .append(", job name : ").append(je.getJobInstance().getJobName())
	                    .append(", status : ").append(je.getStatus())
	                    .append(", start time : ").append(je.getStartTime());
	            }
	        }
	    }

	    return new ResponseEntity<>(html.toString(), HttpStatus.OK);
	}
}
