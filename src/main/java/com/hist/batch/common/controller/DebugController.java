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
		StringBuilder html = new StringBuilder("BatchLog, MultiSync 모니터링.");

		Set<JobExecution> batchLogKeySet = BatchLogFactory.getKeySet();

		if (!batchLogKeySet.isEmpty()) {
			html.append("\n\n<br/><br/>[BatchLog Data] <br/><br/>");

			synchronized (batchLogKeySet) {
		        for (JobExecution je : batchLogKeySet) {
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
		}

		Set<JobExecution> multiSyncKeySet = MultiSyncFactory.getKeySet();
		if (!multiSyncKeySet.isEmpty()) {
			html.append("\n\n<br/><br/>[MultiSync Data] <br/><br/>");
			synchronized (multiSyncKeySet) {
		        for (JobExecution je : multiSyncKeySet) {
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
		}

		if (RunningJobFactory.getSize() > 0) {
			html.append("\n\n<br/><br/>[Running Job List] <br/><br/>");
			List<JobExecution> jobs = RunningJobFactory.getRunningJob();

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
		}

		return new ResponseEntity<String>(html.toString(), HttpStatus.OK);
	}

	@GetMapping("/getJobAllList")
	public ResponseEntity<String> getJobAllList() {
		StringBuilder html = new StringBuilder("Batch Job 전체 모니터링.");

		List<String> jobNames = jobExplorer.getJobNames();

		for (String jobName : jobNames) {
			Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);

			if (!jobExecutions.isEmpty()) {
				html.append("\n<br/>[Running Job] jobName : ").append(jobName);

				synchronized (jobExecutions) {
			        for (JobExecution je : jobExecutions) {
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
			}
		}

		return new ResponseEntity<String>(html.toString(), HttpStatus.OK);
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
