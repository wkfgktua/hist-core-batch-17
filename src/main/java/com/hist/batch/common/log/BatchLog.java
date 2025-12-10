package com.hist.batch.common.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.hist.batch.common.cons.BatchConst;

/**
 * @Desc	 : 배치 로그 (Thread-Safe 최종 버전)
 * @Since	: 2023-07-13
 */
public class BatchLog {
	private AtomicLong totalCnt;
	private AtomicLong errorCnt;

	private volatile boolean showResult;
	private volatile boolean hasError;

	private final List<String> logs;
	private final List<Object> params;

	public BatchLog() {
		this.showResult = false;
		this.hasError = false;

		this.logs = Collections.synchronizedList(new ArrayList<>());
		this.params = Collections.synchronizedList(new ArrayList<>());

		this.totalCnt = new AtomicLong(0);
		this.errorCnt = new AtomicLong(0);
	}

	/**
	 * @Desc	  : 요약 추가
	 */
	public BatchLog add(String log) {
		this.logs.add(log);
		return this;
	}

	/**
	 * @Desc	  : param 추가
	 */
	public BatchLog addParam(Object param) {
		this.params.add(param);
		return this;
	}

	public boolean hasParams() {
		return !params.isEmpty();
	}

	public List<Object> getParam() {
		return params;
	}

	/**
	 * @Desc	  : 에러발견
	 */
	public BatchLog foundError() {
		this.hasError = true;
		this.errorCnt.incrementAndGet();
		return this;
	}

	/**
	 * @Desc	  : 에러수량 추가
	 */
	public BatchLog addErrorCnt(long cnt){
		if (cnt > 0) this.hasError = true;
		this.errorCnt.addAndGet(cnt);
		return this;
	}

	/**
	 * @Desc	  : 전체수량 추가
	 */
	public BatchLog addTotalCnt(long cnt){
		this.totalCnt.addAndGet(cnt);
		return this;
	}

	public long getTotalCnt() {
		return totalCnt.get();
	}

	public long getErrorCnt() {
		return errorCnt.get();
	}

	public boolean getShowResult() {
		return showResult;
	}

	public BatchLog showResult() {
		this.showResult = true;
		return this;
	}

	@Override
	public String toString() {
		synchronized (logs) {
			if (logs.isEmpty()) {
				return "";
			}
			return logs.stream().collect(Collectors.joining(BatchConst.BATCH_LOG_SEPERATOR));
		}
	}

	public boolean isHasError() {
		return hasError;
	}
}