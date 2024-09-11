package com.hist.batch.common.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * @Desc     : 배치 로그 저장소
 * @DBAccess :
 * @Company  :
 * @Project  :
 * @Since    : 2023-07-13
 * @Author   : dlehdusmusic@histmate.co.kr
 *
 */
public class BatchLogFactory {
	private static final Map<JobExecution, BatchLog> batchLogMap = new HashMap<>();

	/**
     * @Desc      : 배치 로그 사용 가능하도록 활성화 합니다. 활성화 하는 측은 책임지고 작업 종료시 clear 하도록 해야 합니다.
     * @Interface :
     * @Param     : je
     * @Return    :
     * @ETC       :
     * @Since     :
     * @Author    :
     *
     */
	public static void setLogAvailable(JobExecution je) {
		synchronized (batchLogMap) {
			if (!batchLogMap.containsKey(je)) {
				batchLogMap.put(je, null);
			}
		}
	}

	/**
     * @Desc      : 배치 로그객체를 얻습니다.
     * @Interface :
     * @Param     : cc
     * @Return    :
     * @ETC       :
     * @Since     :
     * @Author    :
     *
     */
	public static BatchLog getLog(ChunkContext cc) {
		return getLog(cc.getStepContext().getStepExecution());
	}

	/**
     * @Desc      : 배치 로그객체를 얻습니다.
     * @Interface :
     * @Param     : se
     * @Return    :
     * @ETC       :
     * @Since     :
     * @Author    :
     *
     */
	public static BatchLog getLog(StepExecution se) {
		return getLog(se.getJobExecution());
	}

	/**
     * @Desc      : 배치 로그객체를 얻습니다.
     * @Interface :
     * @Param     : je
     * @Return    :
     * @ETC       :
     * @Since     :
     * @Author    :
     *
     */
	public static BatchLog	getLog(JobExecution je) {
		BatchLog rv;
		synchronized (batchLogMap) {
			if (!batchLogMap.containsKey(je)) {
				throw new RuntimeException("batch log is not available for job " + je.getJobInstance().getJobName());
			}

			rv = batchLogMap.get(je);
			if (rv == null) {
				rv = new BatchLog();
			}

			batchLogMap.put(je, rv);
		}
		return rv;
	}

	/**
     * @Desc      : 기록된 로그가 있는지 확인합니다.
     * @Interface :
     * @Param     : je
     * @Return    :
     * @ETC       :
     * @Since     :
     * @Author    :
     *
     */
	public static boolean isLogExist(JobExecution je) {
		return batchLogMap.get(je) != null;
	}

	/**
     * @Desc      : 로그를 클리어합니다. 반드시 작업 종료 후엔 클리어 되어야 합니다.
     * @Interface :
     * @Param     : je
     * @Return    :
     * @ETC       :
     * @Since     :
     * @Author    :
     *
     */
	public static void clear(JobExecution je) {
		batchLogMap.remove(je);
	}

	public static Set<JobExecution> getKeySet() {
		return batchLogMap.keySet();
	}
}
