package com.csu.webapp.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * 线程池工具
 * 
 * @author chenx
 * @since 2020-01-26 16:47:27【天佑武汉】
 * 
 * 
 */
public class ThreadPoolUtils {

	private ThreadPoolUtils() {
	}
 
	/**
	 * 
	 * 考虑到机器配置，这里按照 Dr.Jiang 的建议，将线程池的大小设置为固定一个线程在池中
	 * 
	 */
	private static final ExecutorService threadPool = Executors.newFixedThreadPool(1);

	
	/**
	 * 
	 * FIXME: 处理静态请求的用一个新的线程池
	 * 
	 */
	private static final ExecutorService threadPoolStatic = Executors.newFixedThreadPool(5);

 
	public static ExecutorService getThreadPool() {
		return threadPool;
	}

	public static ExecutorService getThreadpoolstatic() {
		return threadPoolStatic;
	}
}