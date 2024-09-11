package com.hist.batch.common.multiSync.exception;

/**
 *
 * TODO You must describe the purpose of the class.
 *
 * @Project hist-core-batch
 * @Company HIST
 * @since 2024. 05. 08.
 * @author dlehdusmusic@histmate.co.kr
 */
public class MultiSyncException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MultiSyncException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MultiSyncException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MultiSyncException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MultiSyncException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MultiSyncException(Throwable cause) {
		super(cause);
	}

}
