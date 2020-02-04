package com.csu.webapp.util;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoader;

import com.csu.webapp.config.Conf;
import com.csu.webapp.dto.CalcParameter;
import com.csu.webapp.type.SearchBy;

/**
 * 
 * 核心工作流类
 * 
 * @author chenx
 * @since 2020-01-26 19:06:58 【天佑武汉】
 * 
 *
 */

public class KernelWorkflow {

	private static final Logger logger = Logger.getLogger(KernelWorkflow.class
			.getName());
	
	public static final String BASE_PATH = ContextLoader.getCurrentWebApplicationContext().getServletContext()
			.getRealPath("/");

	
	/**
	 * 在线计算模式
	 * 
	 * @param parameter
	 */
	public static void doMiRTMC(CalcParameter parameter) {
		logger.info("保存程序执行参数过程");
		
		String filename = parameter.getResultPath() + Conf.FILE_SEPARATOR
				+ "parameter.txt";
		String fileFastaName = parameter.getResultPath() + Conf.FILE_SEPARATOR
				+ "fasta.txt";
		CalcParameterHelper sysParameterHelper = new CalcParameterHelper(filename, fileFastaName);
		sysParameterHelper.saveObjToFile(parameter);
		
		logger.info("执行matlab过程");
		// windows
		// Resource batResource = new ClassPathResource("/miRTMC/runmatlab_get_top_n.bat");
		
		// linux
		// miRTMC文件夹
		Resource resource = new ClassPathResource("/miRTMC");
		Resource shellResource = new ClassPathResource("/miRTMC/runmatlab_get_top_n.sh");
		String scriptFile;
		
		String args0;
		
		String args1;
		Integer args2;
		String args3;
		String args4;

		try {
			scriptFile = shellResource.getFile().getPath();
			args0 = resource.getFile().getPath();

			// arguments
			if (parameter.getType().intValue() == SearchBy.miRNA_name.getCode()) {
				args1 = "m";
				args2 = parameter.getMiRNAId();

			}else {
				args1 = "g";
				args2 = parameter.getGeneId();
			}
			
			args3 = new ClassPathResource("/miRTMC/Result_MTmatrix2.txt").getFile().getPath();
			args4 = new ClassPathResource("/miRTMC/bmMat.txt").getFile().getPath();
			
			logger.info("脚本文件：" + scriptFile);
			logger.info("Matlab 脚本文件：" + args0);

			boolean ret = ExecuteCommand.executeCommand("bash " + scriptFile + " " + args0 +" " + args1 + " "
					+ args2 + " "+ args3 + "  "+ args4 + "  "+ 50 + "  " +  parameter.getResultPath());
			if(ret) {
				logger.info("success finished executing");
			}
			else {
				logger.error("执行matlab过程出错");
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("执行matlab过程出错" + e.getMessage(),e);
		}
	}
	
	/**
	 * 离线计算模式
	 * 
	 * @param parameter
	 */
	public static void doMiRTMCOffline(CalcParameter parameter) {
		
		logger.info("保存程序执行参数过程");
		
		String filename = parameter.getResultPath() + Conf.FILE_SEPARATOR
				+ "parameter.txt";
		String fileFastaName = parameter.getResultPath() + Conf.FILE_SEPARATOR
				+ "fasta.txt";
		
		CalcParameterHelper sysParameterHelper = new CalcParameterHelper(filename, fileFastaName);
		sysParameterHelper.saveObjToFile(parameter);
		
		logger.info("执行matlab过程");
		
		// linux
		// miRTMC文件夹
		Resource resource = new ClassPathResource("/miRTMC");
		
		
		//miRNA with fasta input
		if(parameter.getType().intValue() == SearchBy.miRNA_name.getCode()) {
			Resource shellResource = new ClassPathResource("/miRTMC/run_new_miRNA.sh");
			String scriptFile;
			
			String args1;

			try {
				scriptFile = shellResource.getFile().getPath();
				args1 = resource.getFile().getPath();
				
				logger.info("脚本文件：" + scriptFile);
	
				boolean ret = ExecuteCommand.executeCommand("bash " + scriptFile + " " + args1 +" " +  parameter.getResultPath());
				if(ret) {
					logger.info("success finished executing");
				}
				else {
					logger.error("执行matlab过程出错");
				}
				try {
					//Next, Send an email to notify the users
					SendMail sMail = new SendMail("miRTMC results for jobID " + parameter.getJobid());
					
					// 不带附件
					sMail.sendEmail(parameter.getEmail(), Conf.MailContent(parameter.getJobid()));
					
				}catch(Exception e) {
					e.printStackTrace();
					logger.error("发送邮件出错" + e.getMessage(),e);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("执行matlab过程出错" + e.getMessage(),e);
			}
		}else {
			
			Resource shellResource = new ClassPathResource("/miRTMC/run_new_gene.sh");
			String scriptFile;
			
			String args1;

			try {
				scriptFile = shellResource.getFile().getPath();
				args1 = resource.getFile().getPath();
				
				logger.info("脚本文件：" + scriptFile);
	
				boolean ret = ExecuteCommand.executeCommand("bash " + scriptFile + " " + args1 +" " +  parameter.getResultPath());
				if(ret) {
					logger.info("success finished executing");
				}
				else {
					logger.error("执行matlab过程出错");
				}
				try {
					//Next, Send an email to notify the users
					SendMail sMail = new SendMail("miRTMC results for jobID " + parameter.getJobid());
					
					// 不带附件
					sMail.sendEmail(parameter.getEmail(), Conf.MailContent(parameter.getJobid()));
					
				}catch(Exception e) {
					e.printStackTrace();
					logger.error("发送邮件出错" + e.getMessage(),e);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("执行matlab过程出错" + e.getMessage(),e);
			}
		}
	}
}
