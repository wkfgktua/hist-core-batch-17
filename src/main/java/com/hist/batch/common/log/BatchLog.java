package com.hist.batch.common.log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hist.batch.common.cons.BatchConst;

/**
 * @Desc	 : 배치 로그
 * @DBAccess :
 * @Company  :
 * @Project  :
 * @Since	:
 * @Author   :
 *
 */
public class BatchLog {
	private long totalCnt;
	private long errorCnt;
	private boolean showResult;
	private boolean hasError;
	private List<String> logs;
	private List<Object> params;

	public BatchLog() {
		showResult = false;
		hasError = false;
		logs = new ArrayList<>();
		totalCnt = 0;
		errorCnt = 0;
	}

	/**
	 * @Desc	  : 요약 추가
	 * @Interface :
	 * @Param	 : log 요약
	 * @Return	: BatchLog
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public BatchLog add(String log) {
		if (logs == null) {
			logs = new ArrayList<>();
		}

		this.logs.add(log);

		return this;
	}

	/**
	 * @Desc	  : param 추가
	 * @Interface :
	 * @Param	 : param Object
	 * @Return	: BatchLog
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public BatchLog addParam(Object param) {
		if (params == null) {
			params = new ArrayList<>();
		}

		this.params.add(param);

		return this;
	}

	public boolean hasParams() {
	    return params != null && !params.isEmpty();
	}

	public List<Object> getParam() {
		return params;
	}

	/**
	 * @Desc	  : 에러발견
	 * @Interface :
	 * @Param	 :
	 * @Return	: BatchLog
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public BatchLog foundError() {
		hasError = true;
		errorCnt++;
		return this;
	}

	/**
	 * @Desc	  : 에러수량 추가
	 * @Interface :
	 * @Param	 :
	 * @Return	: BatchLog
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public BatchLog addErrorCnt(long cnt){
		if(cnt>0)hasError = true;
		errorCnt += cnt;
		return this;
	}

	/**
	 * @Desc	  : 전체수량 추가
	 * @Interface :
	 * @Param	 :
	 * @Return	: BatchLog
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public BatchLog addTotalCnt(long cnt){
		totalCnt += cnt;
		return this;
	}

	/**
	 * @Desc	  : 전체수량 조회
	 * @Interface :
	 * @Param	 :
	 * @Return	: int
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public long getTotalCnt() {
		return totalCnt;
	}

	/**
	 * @Desc	  : 에러수량 조회
	 * @Interface :
	 * @Param	 :
	 * @Return	: int
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public long getErrorCnt() {
		return errorCnt;
	}

	/**
	 * @Desc	  : 처리결과 조희 여부
	 * @Interface :
	 * @Param	 :
	 * @Return	: boolean
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public boolean getShowResult() {
		return showResult;
	}

	/**
	 * @Desc	  : 처리결과보기
	 * @Interface :
	 * @Param	 :
	 * @Return	: BatchLog
	 * @ETC	   :
	 * @Since	 :
	 * @Author	:
	 *
	 */
	public BatchLog showResult() {
		showResult = true;
		return this;
	}

	@Override
	public String toString() {
		if (logs == null || logs.isEmpty()) {
			return "";
		}

		return logs.stream().collect(Collectors.joining(BatchConst.BATCH_LOG_SEPERATOR));
	}

	public boolean isHasError() {
		return hasError;
	}
}
