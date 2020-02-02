package com.csu.webapp.util;

import org.apache.log4j.Logger;

import com.csu.webapp.dto.CalcParameter;
import com.csu.webapp.po.MyThread;

/**
 * 线程，外部参数传递进来，为调用miRTMC计算流程 该线程在某些计算参数下会比较耗时，因此建议不能直接 start， 请配合线程池调用
 * 
 * 
 * @author chenx
 * @since 2020-01-26 18:39:20 【天佑武汉】
 * 
 *
 */
public class DoJobThread extends MyThread {

	private static final Logger logger = Logger.getLogger(DoJobThread.class
			.getName());
	
	public DoJobThread(CalcParameter parameter, String threadName) {
		this.parameter = parameter;
		this.threadName = threadName;
	}

	@Override
	public void run() {
		// 实时计算
		logger.info(this.threadName + " start...");
		KernelWorkflow.doMiRTMC(parameter);
	}
}
