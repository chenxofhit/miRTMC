package com.csu.webapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * Java 调用外部程序执行类
 * 
 * @author kayzhao
 * @author chenx
 * 
 *
 */
public class ExecuteCommand {
	
	private static final Logger logger = Logger.getLogger(ExecuteCommand.class);

	/**
	 * 第二步中：执行matlab过程，是否执行完毕参照 LDAPFlow.class中的判断
	 * 
	 * @param command
	 * @throws IOException
	 */
	public static boolean executeCommand(String cmd) throws IOException {
		Process process = null;
		
		StringBuilder sbResult = new StringBuilder();
		StringBuilder sbError = new StringBuilder();
		
		logger.info("Shell 命令：" + cmd);
		
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
			String sline = null;
			while ((sline = in.readLine()) != null) {
				System.out.println(sline);
				sbResult.append(sline);
				if (sline.contains("finished")) {
					return true;
				}
			}
			if(null != sbResult && sbResult.toString().length()>0) {
				logger.info(sbResult.toString());
			}
			
			in = new BufferedReader(new InputStreamReader(process.getErrorStream(), "utf-8"));
			while ((sline = in.readLine()) != null) {
				System.out.println(sline);
				sbError.append(sline);
			}
			
			if(null != sbError && sbError.toString().length()>0) {
				logger.error(sbError.toString());
			}
			
			in.close();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 第一步中： 执行序列比对，
	 * 
	 * @param command
	 *            要执行的命令
	 * @return true：完成序列比对 <br>
	 *         false：序列比对过程出错
	 * @throws IOException
	 */
	public static boolean executeStretcher(String command) throws IOException {
		Process p = null;
		BufferedReader reader = null;
		System.out.println("序列比对=========\n" + command);
		try {
			p = Runtime.getRuntime().exec(command);
			reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// run_stretcher.sh脚本会在序列比对完成后输出finished
				if (line.contains("finished")) {
					System.out.println("aligment============" + line);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			reader.close();
			return false;
		}
		return false;
	}
}
