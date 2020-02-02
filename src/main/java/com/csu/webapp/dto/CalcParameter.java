package com.csu.webapp.dto;

import java.io.Serializable;

/**
 * 保存运行结果时候必要的参数信息 如：结果放置位置，JobId，Email，Sequence序列
 * 
 * @author chenx
 * @since 2020-01-15 16:36:47
 * 
 *
 */
public class CalcParameter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer type;
	
	private String content;
	
	private Integer geneId;
	
	private Integer miRNAId;
	
	// 结果路径
	private String resultPath;
	
	// 序列，这里只保存了 “>******”这个LncRNA标识信息
	private String sequence;

	// 作业ID
	private String jobid;

	// 用户邮箱
	private String email;
	

	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getResultPath() {
		return resultPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Integer getGeneId() {
		return geneId;
	}

	public void setGeneId(Integer geneId) {
		this.geneId = geneId;
	}

	public Integer getMiRNAId() {
		return miRNAId;
	}

	public void setMiRNAId(Integer miRNAId) {
		this.miRNAId = miRNAId;
	}

	@Override
	public String toString() {
		return "CalcParameter [type=" + type + ", content=" + content + ", geneId=" + geneId + ", miRNAId=" + miRNAId
				+ ", resultPath=" + resultPath + ", sequence=" + sequence + ", jobid=" + jobid + ", email=" + email
				+ "]";
	}
	
}
