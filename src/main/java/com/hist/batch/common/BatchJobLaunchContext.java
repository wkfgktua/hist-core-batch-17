//package com.hist.batch.common;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Supplier;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.batch.core.Job;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// * 배치작업 실행 관련 정보를 가지는 객체
// * @author dlehdusmusic@histmate.co.kr
// */
//@Component
//public class BatchJobLaunchContext implements Filter {
//
//	private static class CurrentLaunchContext implements Cloneable {
//		private boolean						isNoWait;
//		//private CallInfo					callInfo;
//		private Set<String>					exchanges2local	= new HashSet<>();
//		private String						exchange2Ip;
//		private AtomicReference<Exception>	startFail		= new AtomicReference<>();
//		private AtomicReference<Boolean>	isEnd			= new AtomicReference<>();
//		private String						assignedRunId;
//		private Job							job;
//
//		private CurrentLaunchContext() {
//			this.clear();
//		}
//
//		private void clear() {
//			this.isNoWait		= true;
//			//this.callInfo		= null;
//			this.exchanges2local.clear();
//			this.exchange2Ip	= null;
//			this.startFail.set(null);
//			this.isEnd.set(false);
//			this.assignedRunId	= null;
//			this.job			= null;
//		}
//
//		@Override
//		protected Object clone() throws CloneNotSupportedException {
//			return super.clone();
//		}
//	}
//
//	private static ThreadLocal<CurrentLaunchContext> context = ThreadLocal.withInitial(new Supplier<CurrentLaunchContext>() {
//		@Override
//		public CurrentLaunchContext get() {
//			return new CurrentLaunchContext();
//		}
//	});
//
////	@Autowired
////	private BatchOnlineLauncher	batchOnlineLauncher;
//
//	@Autowired
//	private ObjectMapper jsonMapper;
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		HttpServletRequest		hreq;
//		CurrentLaunchContext	currentContext;
//		String					exchange2localInfo;
//		String					exchange2Ip;
//		String					injectedRunid;
//
//		hreq			= (HttpServletRequest)request;
//		currentContext	= context.get();
//		//currentContext.callInfo	= this.batchOnlineLauncher.resolveCallBackInfo(hreq);
//		currentContext.isNoWait	= request.getParameter("nowait") != null;
//		injectedRunid			= request.getParameter("_runId");
//
//		if (StringUtils.isNotEmpty(injectedRunid)) {
//			currentContext.assignedRunId	= injectedRunid;
//		}
//		//개발자용 로컬큐 정보
//		exchange2localInfo	= hreq.getHeader("x-ec-exchange-to-local");
//		if (StringUtils.isNotEmpty(exchange2localInfo)) {
//			Collections.addAll(currentContext.exchanges2local, this.jsonMapper.readValue(exchange2localInfo, String[].class));
//		}
//		exchange2Ip			= hreq.getHeader("x-ec-exchange-to-ip");
//		if (StringUtils.isNotEmpty(exchange2Ip)) {
//			currentContext.exchange2Ip	= exchange2Ip;
//		}
//		try {
//			chain.doFilter(request, response);
//		} finally {
//			context.get().clear();
//		}
//	}
//
//	/**
//	 * 호출자 정보
//	 * @return
//	 */
////	public static CallInfo	getCallInfo() {
////		return context.get().callInfo;
////	}
//
//	/**
//	 * 배치 작업 완료 대기 여부
//	 * @return true이면 대기 하지 않음
//	 */
//	public static boolean isNoWait() {
//		return context.get().isNoWait;
//	}
//
//	/**
//	 * 로컬 큐에 전송해야할 익스체인지인지 확인 합니다 (개발자용)
//	 * @param exchangeName 익스체인지 이름
//	 * @return 로컬 큐에 전송되어야 한다면 true
//	 */
//	public static boolean isExchange2local(String exchangeName) {
//		Set<String>	exchanges2local;
//
//		exchanges2local	= context.get().exchanges2local;
//		return exchanges2local == null ? false : exchanges2local.contains(exchangeName);
//	}
//	/**
//	 * 로컬 큐에 전송해야할 익스체인지 대상 IP (개발자용)
//	 * @return
//	 */
//	public static String getExchange2Ip() {
//		return context.get().exchange2Ip;
//	}
//
//	/**
//	 * 현재 설정된 context를 복제 반환합니다.
//	 * @return
//	 */
//	public static Object copyContext() {
//		try {
//			return context.get().clone();
//		} catch (CloneNotSupportedException ignore) {
//			throw new RuntimeException(ignore);
//		}
//	}
//
//	/**
//	 * 대상 context로 설정합니다
//	 * @param copiedContext
//	 */
//	public static void setContext(Object copiedContext) {
//		if (copiedContext == null) {
//			throw new NullPointerException("copiedContext is null");
//		}
//
//		if (!(copiedContext instanceof CurrentLaunchContext)) {
//			throw new IllegalArgumentException("unsupport context object");
//		}
//
//		context.set((CurrentLaunchContext)copiedContext);
//	}
//
//	/**
//	 * 시작 오류 내용을 담는 래퍼런스 반환
//	 * @return
//	 */
//	public static AtomicReference<Exception> getRefStartFail() {
//		return context.get().startFail;
//	}
//
//	/**
//	 * 작업이 종료되었는지를 여부의 래퍼런스 반환
//	 * @return
//	 */
//	public static AtomicReference<Boolean> getRefIsEnd() {
//		return context.get().isEnd;
//	}
//
//	/**
//	 * 외부에서 지정된 실행 아이디를 반환합니다.
//	 * @return
//	 */
//	public static String getAssignedRunId() {
//		return context.get().assignedRunId;
//	}
//
//	/**
//	 * 실행 대상 job 객체를 반환합니다.
//	 * @return
//	 */
//	public static Job getJob() {
//		return context.get().job;
//	}
//}
