CREATE TABLE multi_sync
(
	ms_id BIGINT NOT NULL AUTO_INCREMENT,
	exe_id BIGINT NOT NULL,
	job_execution_id BIGINT NOT NULL,
	multi_count INT NOT NULL,
	start_dttm DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	end_dttm DATETIME,
	fin_dttm DATETIME,
	fail_dttm DATETIME,
	timeout_dttm DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 1 HOUR),
	status VARCHAR(50) NOT NULL DEFAULT 'START',
	msg VARCHAR(3000),
	PRIMARY KEY (ms_id)
);
CREATE INDEX multi_sync_exe_id_index ON multi_sync(exe_id);
CREATE INDEX multi_sync_job_execution_id_index ON multi_sync(job_execution_id);