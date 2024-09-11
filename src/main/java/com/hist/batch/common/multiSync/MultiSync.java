package com.hist.batch.common.multiSync;

import java.util.HashMap;
import java.util.Map;

import com.hist.core.util.BeanUtils;

import com.hist.batch.common.multiSync.exception.MultiSyncException;
import com.hist.batch.common.multiSync.service.MultiSyncService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Desc	 : Multi Sync
 * @DBAccess :
 * @Company  :
 * @Project  :
 * @Since	:
 * @Author   :
 *
 */
@Slf4j
public class MultiSync {
	enum Status {
        START,
        END,
        FIN,
        FAIL
    }

	private long multiCount;
	//private long exeId;
	private long jobExecutionId;
	private long timeOutSec;
	private boolean endYn;

	private Map<String, String> paramMap;

	private final Object lock = new Object();

	private MultiSyncService multiSyncService;


	public MultiSync(long exeId, long jobExecutionId, long multiCount) {
		init(exeId, jobExecutionId, multiCount,  3600);
	}

	public MultiSync(long exeId, long jobExecutionId, long multiCount,  long timeOutSec) {
		init(exeId, jobExecutionId, multiCount,  timeOutSec);
	}

	private void init(long exeId, long jobExecutionId, long multiCount,  long timeOutSec) {
		//this.exeId = exeId;
		this.jobExecutionId = jobExecutionId;
		this.multiCount = multiCount;
		this.timeOutSec = timeOutSec;
		this.endYn = false;
		paramMap = new HashMap<String, String>();

		this.multiSyncService = (MultiSyncService) BeanUtils.getBean(MultiSyncService.class);

		paramMap.put("exeId", String.valueOf(exeId));
		paramMap.put("jobExecutionId", String.valueOf(this.jobExecutionId));
		paramMap.put("multiCount", String.valueOf(this.multiCount));
		paramMap.put("timeOutSec", String.valueOf(this.timeOutSec));
		paramMap.put("status", Status.START.name());

		multiSyncService.insertMultiSync(paramMap);
	}

	public Status sync() throws MultiSyncException, InterruptedException {
		if(!endYn) {
			paramMap.put("status", Status.END.name());

			if(multiSyncService.updateMultiSyncStatus(paramMap) > 0) {
				this.endYn = true;
			}
		}

		synchronized (lock) {
			long end = System.currentTimeMillis() + (this.timeOutSec * 1000);
			long left = 10;
			long endCount = 0;
			long failCount = 0;

			while (endCount != this.multiCount) {
				left = end - System.currentTimeMillis();
				failCount  = multiSyncService.selectMultiSyncFailCount(paramMap);

				if (left <= 0 || failCount > 0) {
					break;
				}

				log.debug("Multi Sync msId({}) await : {}", paramMap.get("msId"), left);
				lock.wait(5000);

				endCount = multiSyncService.selectMultiSyncEndCount(paramMap);
			}

			if(multiSyncService.selectMultiSyncEndCount(paramMap) == this.multiCount) {
				paramMap.put("status", Status.FIN.name());
				multiSyncService.updateMultiSyncStatus(paramMap);
				return Status.FIN;
			} else if (left <= 0) {
				paramMap.put("msg", "Multi Sync Time Out.");
				paramMap.put("status", Status.FAIL.name());
				multiSyncService.updateMultiSyncStatus(paramMap);
				throw new MultiSyncException("Multi Sync Time Out.");
			} else if (failCount > 0) {
				paramMap.put("msg", "Multi Syncs, the failure count is greater than zero.");
				paramMap.put("status", Status.FAIL.name());
				multiSyncService.updateMultiSyncStatus(paramMap);
				throw new MultiSyncException("Multi Syncs, the failure count is greater than zero.");
			} else {
				paramMap.put("msg", "ETC.");
				paramMap.put("status", Status.FAIL.name());
				multiSyncService.updateMultiSyncStatus(paramMap);
				throw new MultiSyncException("ETC.");
			}
		}
	}

	public boolean setEndYn() {
		if(!endYn) {
			paramMap.put("status", Status.END.name());

			if(multiSyncService.updateMultiSyncStatus(paramMap) > 0) {
				this.endYn = true;
			}
		}

		if(multiSyncService.selectMultiSyncEndCount(paramMap) == this.multiCount) {
			paramMap.put("status", Status.FIN.name());
			multiSyncService.updateMultiSyncStatus(paramMap);
		}

		return endYn;
	}

	public boolean getEndYn() {
		return this.endYn;
	}

	public void updateFail() {
		paramMap.put("status", Status.FAIL.name());
		paramMap.put("msg", "Exit Code Not COMPLETED.");
		multiSyncService.updateMultiSyncStatus(paramMap);
	}
}
