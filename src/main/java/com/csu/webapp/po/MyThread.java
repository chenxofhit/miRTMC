package com.csu.webapp.po;

import com.csu.webapp.dto.CalcParameter;

/**
 * 
 * 线程接口
 * 
 * @author chenx
 * @since 2020-01-31 20:56:06
 *
 */

public abstract class MyThread extends Thread {

	protected CalcParameter parameter;

	protected String threadName;
	
}
