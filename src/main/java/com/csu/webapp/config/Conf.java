package com.csu.webapp.config;

/**
 * 系统变量
 * 
 * @author kayzhao
 * @author chenx
 *
 */
public class Conf {

	/**
	 * 邮箱参数
	 */
	// public static final String HOST = "smtp.csu.edu.cn";
	public static final String HOST = "202.197.64.21";
	public static final String USER = "weilan@csu.edu.cn";
	public static final String PASSWORD = "lanwei3346250";

	/**
	 * 系统符号
	 */
	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	/**
	 * 序列长度的cutoff
	 */
	public static final Integer SEQ_LENGTH_CUTOFF = 15000;

	/**
	 * 运行计算流程反馈结果
	 */

	public static String MIRTMC_STATUS = "Init";

	public static String MailContent(String email, String jobid) {

		StringBuffer link = new StringBuffer(
				"http://bioinformatics.csu.edu.cn/miRTMC/");
		link.append("result/" + email + "/" + jobid);

		String mailContent = "<html><body><p>Dear User,<br>"
				+ "<p>Your job with job id "
				+ jobid
				+ " has been finished on the miRTMC server.  <br>"
				+ "The predicted result is attached with this mail.(The first column is predicted diseases. The second column is corresponding score)<br>"
				+ "The complete results  are available at:<br>" + link
				+ "<p> The results are kept on the server for 3 months. "
				+ "<p>Thanks for using the miRTMC server.<br>"
				+ "--------------<br>" + "<p>The miRTMC Server Team"
				+ "<p>School of Information Science and Engineering"
				+ "<p>Central South University" + "</body></html>";

		return mailContent;
	}
}
