package com.hist.batch.common.multiSync;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.hist.batch.common.multiSync.MultiSync.Status;
import com.hist.batch.common.multiSync.exception.MultiSyncException;

/**
 * @Desc	 : 배치 멀티 병렬처리 싱크 팩토리
 * @DBAccess :
 * @Company  :
 * @Project  :
 * @Since	: 2024-05-08
 * @Author   : dlehdusmusic@histmate.co.kr
 *
 */
public class MultiSyncFactory {

	private static final Map<JobExecution, MultiSync> multiSyncMap = new HashMap<>();

	/**
	 * @Desc	  : 멀티 싱크를 사용 가능하도록 활성화 합니다. 활성화 하는 측은 책임지고 작업 종료시 clear 하도록 해야 합니다.
	 * @Interface :
	 * @Param	 : je
	 * @Return	:
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public static void setMultiSync(JobExecution je) {
		synchronized (multiSyncMap) {
			if (!multiSyncMap.containsKey(je)) {
				if (je != null && je.getJobParameters().getString("exeId") != null && je.getJobParameters().getString("exeId").matches("\\d+") && je.getJobParameters().getString("multiCount") != null && je.getJobParameters().getString("multiCount").matches("\\d+")) {
					if(je.getJobParameters().getString("timeOutSec") != null && je.getJobParameters().getString("timeOutSec").matches("\\d+")) {
						multiSyncMap.put(je, new MultiSync(Long.parseLong(je.getJobParameters().getString("exeId")), je.getId(), Long.parseLong(je.getJobParameters().getString("multiCount")), Long.parseLong(je.getJobParameters().getString("timeOutSec"))));
					} else {
						multiSyncMap.put(je, new MultiSync(Long.parseLong(je.getJobParameters().getString("exeId")), je.getId(), Long.parseLong(je.getJobParameters().getString("multiCount"))));
					}
				}
			} else {
				throw new MultiSyncException("Missing Multisync creation required value for job " + je.getJobInstance().getJobName());
			}
		}
	}

	/**
	 * @Desc	  : 배치 멀티 싱크객체를 얻습니다.
	 * @Interface :
	 * @Param	 : cc
	 * @Return	:
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public static MultiSync getMultiSync(ChunkContext cc) {
		return getMultiSync(cc.getStepContext().getStepExecution());
	}

	/**
	 * @Desc	  : 배치 멀티 싱크객체를 얻습니다.
	 * @Interface :
	 * @Param	 : se
	 * @Return	:
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public static MultiSync getMultiSync(StepExecution se) {
		return getMultiSync(se.getJobExecution());
	}

	/**
	 * @Desc	  : 배치 멀티 싱크객체를 얻습니다.
	 * @Interface :
	 * @Param	 : je
	 * @Return	:
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public static MultiSync getMultiSync(JobExecution je) {
		synchronized (multiSyncMap) {
			if (!multiSyncMap.containsKey(je)) {
				throw new MultiSyncException("Multi sync is not available for job " + je.getJobInstance().getJobName());
			} else {
				return multiSyncMap.get(je);
			}
		}
	}

	/**
	 * @Desc	  : 기록된 멀티 싱크가 있는지 확인합니다.
	 * @Interface :
	 * @Param	 : je
	 * @Return	:
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public static boolean isMultiSyncExist(JobExecution je) {
		return multiSyncMap.containsKey(je);
	}

	/**
	 * @Desc	  : 멀티 싱크를 클리어합니다. 반드시 작업 종료 후엔 클리어 되어야 합니다.
	 * @Interface :
	 * @Param	 : je
	 * @Return	:
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public static void clear(JobExecution je) {
		multiSyncMap.remove(je);
	}

	public static Set<JobExecution> getKeySet() {
		return multiSyncMap.keySet();
	}

	public static Status sync(ChunkContext cc) throws Exception {
		if(isMultiSyncExist(cc.getStepContext().getStepExecution().getJobExecution())) {
			return getMultiSync(cc).sync();
		} else {
			throw new MultiSyncException("Multi Sync Not Exist.");
		}
	}

	public static boolean end(ChunkContext cc) throws Exception {
		if(isMultiSyncExist(cc.getStepContext().getStepExecution().getJobExecution())) {
			return getMultiSync(cc).setEndYn();
		} else {
			throw new MultiSyncException("Multi Sync Not Exist.");
		}
	}
}
